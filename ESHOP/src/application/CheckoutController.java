package application;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CheckoutController implements Initializable {
	
	@FXML
	private Label accountBalanceLabel;

	@FXML
	private CheckBox accountCheckBox;

	@FXML
	private Label orgPriceLabel;
	
	@FXML
    private GridPane gridAll;
	
	@FXML
    private TextField addressField;

    @FXML
    private TextField contactField;
	
	@FXML
    private Label totalLabel;
	
	@FXML
    private Label yourName;
	
	@FXML
	private Label delivery_charge_label;
	
	MyListener2 listener;
	
	private double totalPrice;
	
	private final double delivery_charge = 5.00;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		LoadCart();
		calculateAmount();
		yourName.setText(Control.username);
		accountBalanceLabel.setText("(" + Double.toString(getBalance()) + "$)");
	}
	
	public void LoadCart () {
		String query = "SELECT * FROM cart WHERE who = ?";
		
		listener = new MyListener2 () {
			@Override
			public void ClearCart (Parent root) {
				RemoveNode(root);
			}
		};
		
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);

			statement.setString(1, Control.username);
			ResultSet result = statement.executeQuery();
			
			int added = 0;
			
			while(result.next()) {
				CartCardController cardControl = new CartCardController();
				Parent root = null;
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("CartCard.fxml"));
				try {
					root = loader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				cardControl = loader.getController();
				
				int pid = result.getInt("pid");
				int quantity = result.getInt("quantity");
				
				String query2 = "select * from products where id = ?";
				PreparedStatement statement2 = Control.getConnection().prepareStatement(query2);
				
				statement2.setInt(1,  pid);
				ResultSet result2 = statement2.executeQuery();
				
				if(result2.next()) {
					cardControl.setcustomer(Control.username);
					cardControl.setImg(result2.getString("path"));
					cardControl.setPrice(result2.getDouble("price"));
					cardControl.setproductName(result2.getString("name"));
					cardControl.setQuantity(quantity);
					cardControl.setTotalPrice(result2.getDouble("price") * quantity);
					cardControl.setId(pid);
					cardControl.setListener(listener);
					cardControl.setRoot(root);
				} else {
					continue;
				}
				
				gridAll.add(root, 1, added + 1);
				added++;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void RemoveNode (Parent node) {
		if(node != null) gridAll.getChildren().remove(node);
		calculateAmount();
	}
	
	public void calculateAmount() {
		String query = "SELECT SUM(B.price * A.quantity) AS total_cost FROM cart A, products B WHERE A.who = ? and A.pid = B.id";
		
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);
			
			statement.setString(1, Control.username);
			ResultSet result = statement.executeQuery();
			
			if(result.next()) {
				double total_amount = result.getDouble("total_cost");
				
				if(total_amount < 1) {
					this.orgPriceLabel.setText("0$");
					delivery_charge_label.getStyleClass().remove("strikethrough");
					this.delivery_charge_label.setText("0$");
					this.totalLabel.setText("0$");
					this.totalPrice = 0;
					return;
				}
				
				double total_payable = total_amount + delivery_charge;
				
				this.orgPriceLabel.setText(Double.toString(total_amount) + "$");
				this.delivery_charge_label.setText(Double.toString(delivery_charge) + "$");
				
				if(total_amount > 100) {
					total_payable -= delivery_charge;
					this.delivery_charge_label.getStyleClass().add("strikethrough");
				} else {
					this.delivery_charge_label.getStyleClass().remove("strikethrough");
				}
				
				this.totalLabel.setText(Double.toString(total_payable) + "$");
	
				
				this.totalPrice = total_payable;
			} else {
				this.delivery_charge_label.setText("0$");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void completeCheckout (ActionEvent event) throws IOException {
		
		if(gridAll.getChildren().isEmpty()) {
			return;
		}
		
		String adress = addressField.getText();
		String contact = contactField.getText();
		
		if(adress.isEmpty() || contact.isEmpty()) {
			ShowAlert.ERROR("Missing Info", "Enter shipping address and reciever's contact number");
			return;
		}
		
		if(accountCheckBox.isSelected()) {
			
		} else {
			ShowAlert.ERROR("Error", "Please select a payment method.");
			return;
		}
		
		double userBalance = getBalance();
			
		if(userBalance >= this.totalPrice) {
			updateUserBalance(userBalance - this.totalPrice);
			TakeOrder(adress, contact);
			clearCart();
			calculateAmount();
			
			accountBalanceLabel.setText("(" + Double.toString(getBalance()) + "$)");
			
			ShowAlert.INFORMATION("Successful", "Purchase Successful");
		} else {
			ShowAlert.ERROR("Error Purchasing", "Not enough balance");
		}
	}
	
	public double getBalance() {
		String query = "SELECT balance FROM users where username = ?";
		double userBalance = 0;
		
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);
			
			statement.setString(1, Control.username);
			ResultSet result = statement.executeQuery();
			
			if(result.next()) {
				userBalance = result.getDouble("balance");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return userBalance;
	}
	
	public void updateUserBalance(double balance) {
		String query = "update users set balance = ? where username = ?";
		
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);
			
			statement.setDouble(1, balance);
			statement.setString(2, Control.username);
			statement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void TakeOrder(String adress, String contact) {
		
		String query = "select * FROM cart WHERE who = ?";
		
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);
			
			statement.setString(1, Control.username);
			ResultSet result = statement.executeQuery();
			
			while(result.next()) {
				placeOrder(result.getInt("pid"), Control.username, result.getInt("quantity"), adress, contact);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void placeOrder(int pid, String customerName, int quantity, String location, String contact) {
		String query = "insert into orders (pid, who, quantity, address, phone) values(?, ?, ?, ?, ?)";
		
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);
			
			statement.setInt(1, pid);
			statement.setString(2, customerName);
			statement.setInt(3, quantity);
			statement.setString(4, location);
			statement.setString(5, contact);
			
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void clearCart() {
		gridAll.getChildren().clear();
		
		String query = "DELETE FROM cart WHERE who = ?";
		
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);
			
			statement.setString(1, Control.username);
			statement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void switchToDashboard (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "UserInterface.fxml");
	}
	
	public void switchToAccount (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Account.fxml");
	}
	
	public void switchToOrders (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Orders.fxml");
	}
	
	public void switchToLogin (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Login.fxml", 450, 135);
	}
	
	public void switchToCart (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Cart.fxml");
	}
}
