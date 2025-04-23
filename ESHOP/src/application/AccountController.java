package application;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;


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
		SceneSwitcher.switchTo(event, "UserInterface.fxml");
	}
	
	public void switchToLogin (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Login.fxml", 450, 135);
	}
	
	public void switchToOrders (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Orders.fxml");
	}
	
	public void switchToCart (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Cart.fxml");
	}
}
