package org.javastart.main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.javastart.main.Database.DbConnect;
import java.sql.*;

public class ControllerAddUser {
    @FXML
    private Button btn_AddConfirm;
    @FXML
    private Button btn_AddCancel;
    @FXML
    private TextField tf_AddUsername;
    @FXML
    private TextField tf_AddPassword;

    //function to add user into database.

    public void AddUser(ActionEvent event) {
        DbConnect connection = new DbConnect();
        try (Connection connectDB = connection.getDbConnection()) {
            // 1. Check if username exists
            String checkQuery = "SELECT usernames FROM user WHERE usernames = ?";
            try (PreparedStatement stCheck = connectDB.prepareStatement(checkQuery)) {
                stCheck.setString(1, tf_AddUsername.getText());
                ResultSet rsCheck = stCheck.executeQuery();
                if (rsCheck.next()) {
                    // Username already exists, show alert
                    showAlert("Username already exists!");
                    return; // Exit the method if username exists
                }
            } catch (SQLException e) {
                System.err.println("Error checking username: " + e.getMessage());
            }

            // 2. Username doesn't exist, proceed with insertion
            String insertQuery = "INSERT INTO user (usernames, password, waktu) VALUES (?, ?, 0)";
            try (PreparedStatement st = connectDB.prepareStatement(insertQuery)) {
                st.setString(1, tf_AddUsername.getText());
                st.setString(2, tf_AddPassword.getText());
                st.executeUpdate();
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                System.err.println("Gagal menambahkan data pengguna: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Gagal terhubung ke database: " + e.getMessage());
        }
    }

    // Function to show an alert (replace with your actual alert implementation)
    private void showAlert(String message) {
        // Create an alert object (implementation depends on your UI framework)
        Alert alert = new Alert(Alert.AlertType.ERROR); // Replace with your alert type
        alert.setHeaderText("Error adding user");
        alert.setContentText(message);
        alert.showAndWait();
    }

    //function to close popup window.
    public void cancelAction(ActionEvent event){
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
