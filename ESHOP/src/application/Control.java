package application;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

public class Control {
	private Stage stage; 
	private Scene scene;
	
	private static final String url = "jdbc:mysql://localhost:3306/eshop";
	private static Connection connection;
	
	 static {
		try {
			connection = DriverManager.getConnection(url, "root", "morodmiya");
			System.out.println("Connection with local server established");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	 
	public static Connection getConnection() {
		return connection;
	}

    @FXML
    private PasswordField AdminpassWord;
    
	@FXML
	TextField userName;
	
	public static String username;
	
	@FXML
	PasswordField passWord;
	
	@FXML
    private TextField regName;

    @FXML
    private TextField regemail;

    @FXML
    private PasswordField regpass;

    @FXML
    private PasswordField regpass2;

    @FXML
    private TextField reguserName;
    
	
	public void login (ActionEvent event) throws IOException {
		String name = userName.getText();
		String pass = passWord.getText();
		if (name.isEmpty() || pass.isEmpty()) {
			showLoginErrorAlert(); return ;
		}
		
		username = name;
		
		String query = "SELECT password FROM users WHERE username = ?";
		
		try {
			PreparedStatement statement = connection.prepareStatement(query);

			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			
			if (result.next()) {
				String password = result.getString("password");
				if(pass.equals(password)) {
					System.out.println("Login Successful");
					//PlaySound(); 
					switchToDashboard(event);
				} else {
					showLoginErrorAlert();
				}
			} else {
				showLoginErrorAlert();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insert_user (String name, String username, String pass, String mail) {
		
		String query = "INSERT INTO users (username, name, password, email) VALUES(?,?,?,?)";
		
		try {

			PreparedStatement statement = connection.prepareStatement(query);
			
			statement.setString(1, username); statement.setString(2, name);
			statement.setString(3, pass); statement.setString(4, mail);
			
			statement.execute();
			showRegistrationSuccessDialog();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isUnique (String name) {

		String query = "SELECT password FROM users WHERE username = ?";
		
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, name);
			ResultSet result = statement.executeQuery();
			
			if (result.next()) {
				return false;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void register () {
		String name = regName.getText();
		String username = reguserName.getText();
		String mail = regemail.getText();
		String pass = regpass.getText();
		String pass2 = regpass2.getText();
		
		if (name.isEmpty() || username.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
			showRegistrationErrorAlert3(); return ;
		}
		
		if(pass.equals(pass2)) {
			if (isUnique(username)) {
				insert_user(name, username, pass, mail);
			} else {
				showRegistrationErrorAlert2();
			}
		} else {
			showRegistrationErrorAlert();
		}
	}
	
	public void showLoginErrorAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText("Wrong username or password. Please try again.");

        alert.showAndWait();
    }
	
	public void showRegistrationErrorAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setHeaderText(null);
        alert.setContentText("Passwords don't match, please enter them correctly");

        alert.showAndWait();
    }
	
	public void showRegistrationErrorAlert2() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setHeaderText(null);
        alert.setContentText("Same username already exists, please use another one");

        alert.showAndWait();
    }
	
	public void showRegistrationErrorAlert3() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setHeaderText(null);
        alert.setContentText("Field can't be empty, please fill up the form correctly");

        alert.showAndWait();
    }
	
	private void showRegistrationSuccessDialog() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText(null);
        alert.setContentText("Congratulations! Your registration was successful.\nReturn to Login page");

        alert.showAndWait();
	}
	
	public void switchToRegister (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Registration.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		String css = this.getClass().getResource("application.css").toExternalForm();
		
		scene = new Scene(root);
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.show(); 
	}
	
	public void switchToDashboard (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("UserInterface.fxml"));
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setX(200); stage.setY(80);
		stage.show(); 
	}
	
	public void switchToLogin (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		event.getSource(); 
		String css = this.getClass().getResource("application.css").toExternalForm();
		scene = new Scene(root);
		scene.getStylesheets().add(css);
		
		stage.setScene(scene);
		stage.setX(450); stage.setY(135);
		stage.show(); 
	}
	
	public void switchToAdmin (ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Admin.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		String css = this.getClass().getResource("application.css").toExternalForm();

		scene = new Scene(root);
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		//stage.setX(450); stage.setY(135);
		stage.show(); 
	}
	
	public void AdminLogin(ActionEvent event) throws IOException {
		if(AdminpassWord.getText().equals("12345678")) {
			Parent root = FXMLLoader.load(getClass().getResource("AdminInterface.fxml"));
			Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setX(200); stage.setY(80);
			stage.show();
		} else {
			showLoginErrorAlert();
		}
	}
	

	 public void PlaySound() {
		 try {
	            String audioFilePath = "C:\\Users\\ahsoi\\eclipse-workspace\\HelloFX\\src\\Resources\\collect-ring-15982.mp3";
	            
	            File audioFile = new File(audioFilePath);
	            if (!audioFile.exists()) {
	                System.out.println("Audio file not found: " + audioFilePath);
	                return;
	            }
	            
	            URI uri = audioFile.toURI();
	            String mediaPath = uri.toString();

	            Media media = new Media(mediaPath);
	            MediaPlayer mediaPlayer = new MediaPlayer(media);

	            mediaPlayer.play();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	 }
}