package org.javastart.main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.javastart.main.Database.DbConnect;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControllerLogin {
    @FXML
    private Button btn_login;
    @FXML
    private TextField tf_username;
    @FXML
    private PasswordField tf_password;

    public void loginAction(ActionEvent event) {
        DbConnect connection = new DbConnect();
        try (Connection connectDB = connection.getDbConnection()) {
            String query = "SELECT * FROM Admin WHERE username=? AND password=?";
            try (PreparedStatement statement = connectDB.prepareStatement(query)) {
                statement.setString(1, tf_username.getText());
                statement.setString(2, tf_password.getText());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + tf_username.getText());
                    openApplicationLayer(event);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
                }
            } catch (SQLException e) {
                System.err.println("Error executing login query: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openApplicationLayer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/javastart/main/Controller/Application.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to open Application layer: " + e.getMessage());
        }
    }
}
