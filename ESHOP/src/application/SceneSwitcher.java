package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneSwitcher {

    private static Stage stage;
    private static Scene scene;

    public static void switchTo(ActionEvent event, String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlFile));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    public static void switchTo(ActionEvent event, String fxmlFile, int X, int Y) throws IOException {
        Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlFile));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        scene = new Scene(root);
        stage.setScene(scene);
        stage.setX(X); stage.setY(Y);
        stage.show();
    }
}