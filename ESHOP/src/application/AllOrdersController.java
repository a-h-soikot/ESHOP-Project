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
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AllOrdersController implements Initializable {

    @FXML
    private GridPane gridAll;
    
    @FXML
    private TextField searchText;
    
    private FilterData lastUsedFilter;

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
    
    
    @FXML
    private void onFilterClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FilterDialog.fxml"));
            Parent root = loader.load();
            
            FilterDialogController controller = loader.getController();
            if (lastUsedFilter != null) {
                controller.setFilterData(lastUsedFilter);
            }
            
            Stage stage = new Stage();
            stage.setTitle("Filter Orders");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait for dialog to close

            
            FilterData filters = controller.getFilterData();
            lastUsedFilter = filters;

            if (filters != null) {
            	gridAll.getChildren().clear();
                filterOrders(filters, searchText == null ? null : searchText.getText().toLowerCase());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    @FXML
    private void orderSearch() {
    	if((searchText == null || searchText.getText().trim().isEmpty()) && lastUsedFilter == null) {
    		return;
    	}
    	gridAll.getChildren().clear();
    	filterOrders(lastUsedFilter, searchText.getText().toLowerCase());
    }
    
    
    private void filterOrders(FilterData filter, String searchString) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String query = "SELECT * FROM products INNER JOIN orders on id = pid WHERE 1=1";

                if (filter != null && filter.startDate != null) query += " AND DATE(date) >= '" + filter.startDate + "'";
                if (filter != null && filter.endDate != null) query += " AND DATE(date) <= '" + filter.endDate + "'";
                if (filter != null && !filter.userId.isEmpty()) query += " AND who = '" + filter.userId + "'";
                
                if (!(searchString == null || searchString.trim().isEmpty())) {
                	
                    String[] searchWords = searchString.split("\\s+");
                    
                    String queryToAdd = " AND (";
                    queryToAdd += " name LIKE '%" + searchWords[0] + "%'";
                    
                    for(int i = 1; i < searchWords.length; i++) {
                    	queryToAdd += " OR name like '%" + searchWords[i] + "%'";
                    }
                    
                    queryToAdd += ")";
                    
                    query += queryToAdd;
                }

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
                           final String imgPath = result.getString("path");
                           final String price = result.getString("price");
                           final String name = result.getString("name");
                           
                           FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderCard.fxml"));
                           Parent root = loader.load();
                           OrderCardController cardControl = loader.getController();
                           
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
        new Thread(task).start();
    }




	public void switchToDashboard (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "AdminInterface.fxml");
	}
	
	public void switchToLogin (ActionEvent event) throws IOException {
		SceneSwitcher.switchTo(event, "Login.fxml", 450, 135);
	}
}
