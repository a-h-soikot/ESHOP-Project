package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardController {
	 @FXML
	 private ImageView productImage;

	 @FXML
	 private Label productName;

	 @FXML
	 private Label productPrice;
	 
	 private Image image;
	 
	 private String path;
	 
	 int pid;
	 
	 int available;
	 
	 MyListener listener;
	 
	 public void setListener (MyListener listener) {
		 this.listener = listener;
	 }
	 
	 public void setId(int id) {
		 this.pid = id;
	 }
	 
	 public void setQuantity(int num) {
		 this.available = num;
	 }
	 
	 public void setImg () {
		 path = "file:/C:/Users/ahsoi/eclipse-workspace/HelloFX/src/Resources/fruitss.jpg";
		 image = new Image(path);
		 productImage.setImage(image);
	 }
	 
	 public void setImg2 () {
		 path = "file:/C:/Users/ahsoi/eclipse-workspace/HelloFX/src/Resources/MainLogo2.jpg";
		 image = new Image(path);
		 productImage.setImage(image);
	 }
	 
	 public void setImg (String location) {
		 image = new Image(location);
		 productImage.setImage(image);
		 path = location;
	 }
	 
	 public String getPath() {
		 return this.path;
	 }
	 
	 public void setName (String name) {
		 productName.setText(name);
	 }
	 
	 public void setPrice (double price) {
		 productPrice.setText(Double.toString(price) + "$");
	 }
	 
	 public int getId() {
		 return this.pid;
	 }
	 
	 public int getQuantity() {
		 String query = "select quantity from products where id = ?";
			
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);
				
			statement.setInt(1, this.pid);
				
			ResultSet result = statement.executeQuery();
			
			if(result.next()) {
				return result.getInt("quantity");
			}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		return 0;
	 }
	 
	 public Image getImg () {
		 return image;
	 }
	 
	 public String getName () {
		 return productName.getText();
	 }
	 
	 public String getPrice () {
		 return productPrice.getText();
	 }
	 
	 public void ImageClicked () {
		 listener.ShowItemDetails(this);
	 }
	 
}
