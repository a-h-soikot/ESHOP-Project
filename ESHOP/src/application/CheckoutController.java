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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
	
	MyListener2 listener;
	
	private double totalPrice;
	
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
				this.totalLabel.setText(Double.toString(total_amount) + "$");
				this.orgPriceLabel.setText(Double.toString(total_amount) + "$");
				this.totalPrice = total_amount;
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
			ErrorAlert3();
			return;
		}
		
		if(accountCheckBox.isSelected()) {
			
		} else {
			ErrorAlert2();
			return;
		}
		
		double userBalance = getBalance();
			
		if(userBalance >= this.totalPrice) {
			updateUserBalance(userBalance - this.totalPrice);
			TakeOrder(adress, contact);
			clearCart();
			calculateAmount();
			
			accountBalanceLabel.setText("(" + Double.toString(getBalance()) + "$)");
			
			SuccessDialog();
		} else {
			ErrorAlert();
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
	
	public void ErrorAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Purchasing");
        alert.setHeaderText(null);
        alert.setContentText("Not enough balance");

        alert.showAndWait();
    }
	
	public void ErrorAlert2() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Please select a payment method.");

        alert.showAndWait();
    }
	
	public void ErrorAlert3() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Missing Info");
        alert.setHeaderText(null);
        alert.setContentText("Enter shipping address and reciever's contact number");

        alert.showAndWait();
    }
	
	private void SuccessDialog() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Successful");
        alert.setHeaderText(null);
        alert.setContentText("Purchase Successful");

        alert.showAndWait();
	}
	
	public void switchToDashboard (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("UserInterface.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show(); 
	}
	
	public void switchToAccount (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Account.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void switchToOrders (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Orders.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void switchToLogin (ActionEvent event) throws IOException {
		Control control = new Control();
		control.switchToLogin(event);
	}
	
	public void switchToCart (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Cart.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
