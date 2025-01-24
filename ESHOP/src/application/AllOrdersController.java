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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AllOrdersController implements Initializable {

    @FXML
    private GridPane gridAll;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        loadOrdersAsync();
    }

    private void loadOrdersAsync() {
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String query = "SELECT * FROM orders";
                
                try (PreparedStatement statement = Control.getConnection().prepareStatement(query);
                     ResultSet result = statement.executeQuery()) {
                    
                    int added = 0;
                    
                    while (result.next()) {

                        final int pid = result.getInt("pid");
                        final int quantity = result.getInt("quantity");
                        final String customer = result.getString("who");
                        final String address = result.getString("address");
                        final String phone = result.getString("phone");
                        final String date = result.getTimestamp("date").toString();
                        
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderCard.fxml"));
                        Parent root = loader.load();
                        OrderCardController cardControl = loader.getController();
                        
                        String query2 = "SELECT * FROM products WHERE id = ?";
                        try (PreparedStatement statement2 = Control.getConnection().prepareStatement(query2)) {
                        	statement2.setInt(1,  pid);
                        	ResultSet result2 = statement2.executeQuery();
                            if (result2.next()) {
                                final String imgPath = result2.getString("path");
                                final String price = result2.getString("price");
                                final String name = result2.getString("name");
                                
                                Platform.runLater(() -> {
                                    cardControl.setcustomer(customer);
                                    cardControl.setImg(imgPath);
                                    cardControl.setPrice(price);
                                    cardControl.setproductName(name);
                                    cardControl.setQuantity(quantity);
                                    cardControl.setAdress(address);
                                    cardControl.setContact(phone);
                                    cardControl.setDate(date);
                                });
                            }
                        } catch (SQLException e) {
                			e.printStackTrace();
                		}
                        
                        final int row = added + 1;
                        Platform.runLater(() -> gridAll.add(root, 1, row));
                        added++;
                        
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                return null;
            }
        };
        
        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }



	
	public void switchToDashboard (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("AdminInterface.fxml"));
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
}
