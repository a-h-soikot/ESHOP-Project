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

public class OrderController implements Initializable {
	
	@FXML
    private GridPane gridAll;
	
	@FXML
    private Label yourName;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		String query = "SELECT * FROM orders WHERE who = ?";
		
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);

			statement.setString(1, Control.username);
			
			ResultSet result = statement.executeQuery();
			
			int added = 0;
			
			while(result.next()) {
				OrderCardController cardControl = new OrderCardController();
				Parent root = null;
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderCard.fxml"));
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
					cardControl.setPrice(result2.getString("price"));
					cardControl.setproductName(result2.getString("name"));
					cardControl.setQuantity(quantity);
					cardControl.setAdress(result.getString("address"));
					cardControl.setContact(result.getString("phone"));
					cardControl.setDate(result.getTimestamp("date").toString());
				} else {
					continue;
				}
				
				gridAll.add(root, 1, added + 1);
				added++;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		yourName.setText(Control.username);
	}
	
	public void GridClear (ActionEvent event) throws IOException {
		gridAll.getChildren().clear();
		System.out.println("Done Clearing");
	}
	
	public void switchToDashboard (ActionEvent event) throws IOException {
		Control control = new Control();
		control.switchToDashboard(event);
	}
	
	public void switchToAccount (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Account.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setX(200); stage.setY(80);
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
		stage.setX(200); stage.setY(80);
		stage.show();
	}
}
