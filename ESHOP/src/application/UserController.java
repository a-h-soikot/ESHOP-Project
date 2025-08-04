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
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

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
    private TextField searchText;
    
    public static String searchString;
    
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
				ShowAlert.ERROR("Error","Not enough product in stock");
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
				ShowAlert.INFORMATION("Successful", "Succesfully Added");
			} else {
				
				subtractQuantity(quantity);
				
				query = "INSERT INTO cart (pid, who, quantity) VALUES(?,?,?)";
				statement = Control.getConnection().prepareStatement(query);
				
				statement.setInt(1, this.pid); 
				statement.setString(2, Control.username);
				statement.setInt(3, quantity);
				
				statement.execute();
				
				ShowAlert.INFORMATION("Successful", "Succesfully Added");
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
	
	@FXML
	public void onSearchButtonClick(ActionEvent event) throws IOException {
		if(searchText == null || searchText.getText().trim().isEmpty()) {
			return;
		}
		searchString = searchText.getText().trim().toLowerCase();
		
		SceneSwitcher.switchTo(event, "Search.fxml");
	}
	
	@FXML
	public void onFilterButtonClick() {
		
	}
	
	public static String getSearchString () {
		return searchString;
	}
	
	public void switchToLogin (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Login.fxml", 450, 135);
	}
	
	public void switchToAccount (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Account.fxml");
	}
	
	public void switchToOrders (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Orders.fxml");
	}
	
	public void switchToCart (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Cart.fxml");
	}
}