package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AdminController implements Initializable {

    @FXML
    private ImageView showImg;

    @FXML
    private Label yourName;
    
    @FXML
    private TextField productname;

    @FXML
    private TextField productprice;

    @FXML
    private TextField productquantity;
    
    private String path;
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	
	}

    @FXML
    void choosefile(ActionEvent event) {
    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("Choose product image");
    	
    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg");
        chooser.getExtensionFilters().add(extFilter);
    	
    	File selectedFile = chooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            showImg.setImage(image);
            this.path = selectedFile.toURI().toString();
        } else {
            System.out.println("No file selected.");
        }
    }
    
    public void AddProduct () {
    	
    	if(path == null || productname.getText().isEmpty() || productprice.getText().isEmpty() || productquantity.getText().isEmpty()) {
    		ErrorAlert("Please provide information."); return;
    	}
    	
    	if(Double.parseDouble(productprice.getText()) <= 0) {
    		ErrorAlert("Invalid Price"); return;
    	}
    	
    	if(Integer.parseInt(productquantity.getText()) < 1) {
    		ErrorAlert("Invalid Quantity"); return;
    	}
    	
    	String query = "INSERT INTO products (name, price, path, quantity) VALUES(?,?,?,?)";
    	
    	try {

			PreparedStatement statement = Control.getConnection().prepareStatement(query);
			
			statement.setString(1, productname.getText()); 
			statement.setDouble(2, Double.parseDouble(productprice.getText()));
			statement.setString(3, this.path); 
			statement.setInt(4, Integer.parseInt(productquantity.getText()));
			
			statement.execute();
			SuccessDialog();
			clearFields();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void clearFields() {
        showImg.setImage(null); 

        productname.clear();
        productprice.clear();
        productquantity.clear();

        path = null;
    }
    
    private void SuccessDialog() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Successful");
        alert.setHeaderText(null);
        alert.setContentText("Succesfully Added");

        alert.showAndWait();
	}
    
    public void ErrorAlert(String Content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(Content);

        alert.showAndWait();
    }
    
    public void switchToLogin(ActionEvent event) throws IOException {
    	Control control = new Control();
    	control.switchToLogin(event);
    }
    
    public void switchToAllOrders (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("AllOrders.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setX(200); stage.setY(80);
		stage.show();
	}

}
