package application;

	
import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Main extends Application {
	
	@Override
	public void start(Stage stage) {
		try {

			Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));

			Image icon = new Image(getClass().getResource("/Resources/icon.png").toExternalForm());
			
			Scene scene = new Scene(root);
			
			stage.setScene(scene); stage.setResizable(false);
			stage.setTitle("E Shop"); 
			stage.getIcons().add(icon);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void stop() throws SQLException {
        if (Control.getConnection() != null && !Control.getConnection().isClosed()) {
            Control.getConnection().close();
            System.out.println("Database connection closed.");
        }
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}

