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
import javafx.stage.Stage;

public class AccountController implements Initializable {
	
	 @FXML
	 private Label balance;

	 @FXML
	 private Label email;

	 @FXML
	 private Label name;

	 @FXML
	 private Label username;
	 
	 @FXML
	 private Label yourName;
	 
	 @Override
	 public void initialize(URL arg0, ResourceBundle arg1) {
		 
		 String query = "SELECT * FROM users WHERE username = ?";
		 
		 try {
			 PreparedStatement statement = Control.getConnection().prepareStatement(query);
			 statement.setString(1, Control.username);
			 ResultSet result = statement.executeQuery();
			 
			 if (result.next()) {
				 name.setText(result.getString("name"));
				 username.setText(Control.username);
				 email.setText(result.getString("email"));
				 balance.setText(result.getString("balance") + "$");
			 } else {
				 System.out.println("SQL Error");
			 }
			 
		 } catch (SQLException e) {
			 e.printStackTrace();
		}
		 
		 yourName.setText(Control.username);
	 };
	
	public void switchToDashboard (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("UserInterface.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		//String css = this.getClass().getResource("UserInterface.css").toExternalForm();
		Scene scene = new Scene(root);
		//scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.setX(200); stage.setY(80);
		stage.show();
	}
	
	public void switchToLogin (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		String css = this.getClass().getResource("application.css").toExternalForm();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.setX(450); stage.setY(135);
		stage.show();
	}
	
	public void switchToOrders (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Orders.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setX(200); stage.setY(80);
		stage.show();
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
