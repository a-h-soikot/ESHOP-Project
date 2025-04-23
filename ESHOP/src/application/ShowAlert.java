package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ShowAlert {
	
	public static void ERROR (String Title, String Content) {
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(Title);
        alert.setHeaderText(null);
        alert.setContentText(Content);

        alert.showAndWait();
	}
	
	public static void INFORMATION (String Title, String Content) {
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(Title);
        alert.setHeaderText(null);
        alert.setContentText(Content);

        alert.showAndWait();
	}
	
}
