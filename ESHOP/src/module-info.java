module HelloFX {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires java.sql;
	requires java.desktop;
	requires javafx.media;
	requires java.management;
	
	opens application to javafx.graphics, javafx.fxml;
}
