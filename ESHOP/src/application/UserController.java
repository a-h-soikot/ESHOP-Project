package application;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class UserController implements Initializable {
	
	@FXML
    private GridPane gridAll;
	
	@FXML
    private Label selectedPrice;

    @FXML
    private GridPane gridTop;
    
    @FXML
    private ImageView itemImage;

    @FXML
    private Label itemLabel;
    
    @FXML
    private Label yourName;
    
    @FXML
    private TextField quantityField;
    
    @FXML
    private Label availableLabel;
    
    @FXML
    private Button buyingButton;
    
    int pid;
    
    int available_product;
    
    String imgpath;
	
    MyListener listener;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		yourName.setText(Control.username);
		
		importTopPicks();
		
		importAllItems();
	}
	
	void importTopPicks() {
	    Task<Void> task = new Task<>() {
	        @Override
	        protected Void call() throws Exception {
	            String query = "SELECT * FROM toppicks inner join products on toppicks.pid = products.id";
	           
	            
	            try (PreparedStatement statement = Control.getConnection().prepareStatement(
	                     query, 
	                     ResultSet.TYPE_SCROLL_INSENSITIVE, 
	                     ResultSet.CONCUR_READ_ONLY)) {
	                
	                ResultSet result = statement.executeQuery();
	                
	                // Move to the last row
	                result.last();
	                int rowCount = result.getRow();
	                int added = 0;
	                
	                // Traverse from last to first
	                for (int i = rowCount; i > 0; i--) {
	                    result.absolute(i);
	                    final int pid = result.getInt("id");
	                    final int quantity = result.getInt("quantity");
	                    final String name = result.getString("name");
	                    final String path = result.getString("path");
	                    final double price = result.getDouble("price");
	                    final int index = added + 1;
	                    
	                    Platform.runLater(() -> {
	                        listener = new MyListener() {
	                            @Override
	                            public void ShowItemDetails(CardController card) {
	                                setChosenItem(card);
	                            }
	                        };
	                        
	                        try {
	                            FXMLLoader loader = new FXMLLoader(getClass().getResource("cards.fxml"));
	                            Parent root = loader.load();
	                            
	                            CardController cardControl = loader.getController();
	                            
	                            cardControl.setId(pid);
	                            cardControl.setQuantity(quantity);
	                            cardControl.setName(name);
	                            cardControl.setImg(path);
	                            cardControl.setPrice(price);
	                            cardControl.setListener(listener);
	                            
	                            gridTop.add(root, index, 1);
	                          
	                        } catch (IOException e) {
	                            e.printStackTrace();
	                        }
	                       
	                    });
	                    added += 1;
	                }
	            }
	            return null;
	        }
	    };
	    
	    new Thread(task).start();
	}

	public void importAllItems() {
	    Task<Void> task = new Task<>() {
	        @Override
	        protected Void call() throws Exception {
	            String query = "SELECT * FROM products";
	            int row = 0, col = 0;
	            
	            try (PreparedStatement statement = Control.getConnection().prepareStatement(
	                     query, 
	                     ResultSet.TYPE_SCROLL_INSENSITIVE, 
	                     ResultSet.CONCUR_READ_ONLY)) {
	                
	                ResultSet result = statement.executeQuery();
	                
	                // Move to the last row
	                result.last();
	                int rowCount = result.getRow();
	                
	                // Traverse from last to first
	                for (int i = rowCount; i > 0; i--) {
	                    result.absolute(i);
	                    
	                    final int pid = result.getInt("id");
	                    final int quantity = result.getInt("quantity");
	                    final String name = result.getString("name");
	                    final String path = result.getString("path");
	                    final double price = result.getDouble("price");
	                    final int currentRow = row;
	                    final int currentCol = col;
	                    
	                    Platform.runLater(() -> {
	                        listener = new MyListener() {
	                            @Override
	                            public void ShowItemDetails(CardController card) {
	                                setChosenItem(card);
	                            }
	                        };
	                        
	                        try {
	                            FXMLLoader loader = new FXMLLoader(getClass().getResource("cards.fxml"));
	                            Parent root = loader.load();
	                            
	                            CardController cardControl = loader.getController();
	                            
	                            cardControl.setId(pid);
	                            cardControl.setQuantity(quantity);
	                            cardControl.setName(name);
	                            cardControl.setImg(path);
	                            cardControl.setPrice(price);
	                            cardControl.setListener(listener);
	                            
	                            gridAll.add(root, currentCol + 1, currentRow + 1);
	                        } catch (IOException e) {
	                            e.printStackTrace();
	                        }
	                    });
	                    
	                    col++;
	                    if (col % 4 == 0) {
	                        col = 0;
	                        row++;
	                    }
	             
	                }
	            }
	            return null;
	        }
	    };
	    
	    new Thread(task).start();
	}
	
	public void setChosenItem (CardController card) {
		itemLabel.setText(card.getName());
		itemImage.setImage(card.getImg());
		selectedPrice.setText(card.getPrice());
		this.imgpath = card.getPath();
		
		this.pid = card.getId();
		availableLabel.setText(Integer.toString(card.getQuantity()) + " available");
		available_product = card.getQuantity();
	}
	
	@SuppressWarnings("resource")
	public void cart (ActionEvent event) {
		String query;
		
		try {
		
			int quantity = 1;
			String str = quantityField.getText();
			if(!str.isEmpty()) quantity = Integer.parseInt(str);
			
			if(quantity > available_product || quantity < 1) {
				ErrorAlert();
				return;
			}
			
			query = "select quantity from cart where pid = ? and who = ?";
			PreparedStatement statement = Control.getConnection().prepareStatement(query);

			statement.setInt(1, this.pid);
			statement.setString(2, Control.username);
			
			ResultSet result = statement.executeQuery();
			
			if (result.next()) {
				quantity += result.getInt("quantity");
				
				subtractQuantity(quantity - result.getInt("quantity"));
				
				query = "update cart set quantity = ? where pid = ? and who = ?";
				statement = Control.getConnection().prepareStatement(query);
				
				statement.setInt(1, quantity);
				statement.setInt(2, this.pid);
				statement.setString(3, Control.username);
				
				statement.execute();
				SuccessDialog();
			} else {
				
				subtractQuantity(quantity);
				
				query = "INSERT INTO cart (pid, who, quantity) VALUES(?,?,?)";
				statement = Control.getConnection().prepareStatement(query);
				
				statement.setInt(1, this.pid); 
				statement.setString(2, Control.username);
				statement.setInt(3, quantity);
				
				statement.execute();
				SuccessDialog();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void subtractQuantity(int quantity) {
		String query = "update products set quantity = quantity - " + Integer.toString(quantity) + " where id = ?";
		
		try {
			PreparedStatement statement = Control.getConnection().prepareStatement(query);
			
			statement.setInt(1, this.pid);
			
			statement.execute();
			
			available_product -= quantity;
			availableLabel.setText(Integer.toString(available_product) + " available");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void SuccessDialog() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Successful");
        alert.setHeaderText(null);
        alert.setContentText("Succesfully Added");

        alert.showAndWait();
	}
	
	public void ErrorAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error adding to cart");
        alert.setHeaderText(null);
        alert.setContentText("Not enough product in stock");

        alert.showAndWait();
    }
	
	
	public void switchToLogin (ActionEvent event) throws IOException {
		Control object = new Control();
		object.switchToLogin(event);
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
	
	public void switchToCart (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Cart.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}