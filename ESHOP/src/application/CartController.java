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
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CartController implements Initializable {
	
	@FXML
    private GridPane gridAll;
	
	@FXML
    private Label totalLabel;
	
	@FXML
    private Label yourName;
	
	
	MyListener2 listener;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		LoadCart();
		calculateAmount();
		yourName.setText(Control.username);
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
					cardControl.setId(pid);;
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
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void switchToCheckout (ActionEvent event) throws IOException {
		
		Parent root = FXMLLoader.load(getClass().getResource("Checkout.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
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
}
