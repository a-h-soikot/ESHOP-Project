package application;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class OrderCardController {
	
	@FXML
    private AnchorPane anchorPane;

    @FXML
    private Label customerName;

    @FXML
    private ImageView productImage;

    @FXML
    private Label productName;
    
    @FXML
    private Label productPrice;
    
    @FXML
    private Label quantityLabel;
    
    @FXML
    private Label phone;

    @FXML
    private Label address;

    @FXML
    private Label orderDate;
    
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
    	customerName.setText(name);
    }
    
    public void setproductName (String name) {
    	productName.setText(name);
    }
    
    public void setImg (String path) {
    	productImage.setImage(new Image(path));
    }
    
    public void setPrice(String price) {
    	productPrice.setText(price);
    }
    
    public void setAdress(String location) {
    	address.setText(location);
    }
    
    public void setContact(String contact) {
    	phone.setText(contact);
    }
    
    public void setDate(String _Date) {
    	orderDate.setText(_Date);
    }

}
