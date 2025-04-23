package application;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class CartCardController {
	
	@FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView productImage;

    @FXML
    private Label productName;
    
    @FXML
    private Label productPrice;
    
    @FXML
    private Label quantityLabel;
    
    @FXML
    private Button TotalButton;
    
    String customerName;
    
    int pid;
    
    Parent root;
    
    MyListener2 listener;
    
    public void setListener(MyListener2 listener2) {
    	this.listener = listener2;
    }
    
    public void setRoot(Parent root2) {
    	this.root = root2;
    }
    
    public void setId(int id) {
    	this.pid = id;
    }
    
    public int getId() {
    	return this.pid;
    }
    
    public void setQuantity (int num) {
    	quantityLabel.setText("x" + Integer.toString(num));
    }
    
    public void setcustomer (String name) {
    	customerName = name;
    }
    
    public void setproductName (String name) {
    	productName.setText(name);
    }
    
    public void setImg (String path) {
    	productImage.setImage(new Image(path));
    }
    
    public void setPrice(double price) {
    	productPrice.setText(Double.toString(price) + "$");
    }
    
    public void setTotalPrice(double price) {
    	TotalButton.setText("Total: " + Double.toString(price) + "$");
    }
    
    public void cancel (ActionEvent event) throws IOException {
    	addBack();
    	String query = "DELETE FROM cart WHERE pid = ? and who = ?";
		
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);
			
			statement.setInt(1, this.pid);
			statement.setString(2, this.customerName);
			statement.execute();
			
			listener.ClearCart(root);

		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void addBack() {
    	String query = "select quantity FROM cart WHERE pid = ? and who = ?";
		
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);
			
			statement.setInt(1, this.pid);
			statement.setString(2, this.customerName);
			
			ResultSet result = statement.executeQuery();
			
			int quantity = 0;
			if(result.next()) {
				quantity = result.getInt("quantity");
			}
			
			query = "update products set quantity = quantity + " + Integer.toString(quantity) + " where id = ?";
			statement = Control.getConnection().prepareStatement(query);
			statement.setInt(1, this.pid);
			
			statement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    

}
