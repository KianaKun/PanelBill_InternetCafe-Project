package org.javastart.main.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.javastart.main.Database.DbConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControllerLoginUser {
    @FXML
    private TextField tf_username;
    @FXML
    private PasswordField tf_password;

    @FXML
    private Label timeLabel;
    private Connection connectDB;
    private ScheduledExecutorService executor;
    private boolean isLoggedIn = false; // Menyimpan status login

    public void loginAction(ActionEvent event) {
        // Jika sudah login, beri pesan peringatan dan keluar dari metode
        if (isLoggedIn) {
            showAlert(Alert.AlertType.WARNING, "Already logged in", "You are already logged in.");
            return;
        }

        DbConnect connection = new DbConnect();
        connectDB = connection.getDbConnection();
        String query = "SELECT waktu FROM user WHERE usernames=? AND password=?";
        try (PreparedStatement statement = connectDB.prepareStatement(query)) {
            statement.setString(1, tf_username.getText());
            statement.setString(2, tf_password.getText());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int waktu = resultSet.getInt("waktu");
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + tf_username.getText());
                updateTimeLabel(waktu);

                // Set status login menjadi true
                isLoggedIn = true;

                // Jalankan tugas pengurangan waktu hanya jika belum berjalan
                if (executor == null || executor.isShutdown()) {
                    executor = Executors.newSingleThreadScheduledExecutor();
                    executor.scheduleAtFixedRate(this::reduceTime, 0, 1, TimeUnit.SECONDS);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        } catch (SQLException e) {
            System.err.println("Error executing login query: " + e.getMessage());
        }
    }

    private boolean timeExpiredAlertShown = false;

    private void reduceTime() {
        try {
            // Mendapatkan waktu dari database
            String timeQuery = "SELECT waktu FROM user WHERE usernames=? AND password=?";
            try (PreparedStatement timeStatement = connectDB.prepareStatement(timeQuery)) {
                timeStatement.setString(1, tf_username.getText());
                timeStatement.setString(2, tf_password.getText());
                ResultSet resultSet = timeStatement.executeQuery();
                if (resultSet.next()) {
                    int waktu = resultSet.getInt("waktu");
                    Platform.runLater(() -> {
                        if (waktu <= 0 && !timeExpiredAlertShown) {
                            showAlert(Alert.AlertType.WARNING, "Time Expired", "Your time has expired. Please refill your time at the billing admin.");
                            timeExpiredAlertShown = true; // Tandai bahwa alert sudah ditampilkan
                            stopExecutor(); // Menghentikan scheduler jika waktu sudah habis
                        } else {
                            // Jika waktu belum habis, kurangi waktu di database
                            if (waktu > 0) {
                                String updateQuery = "UPDATE user SET waktu = waktu - 1";
                                try (PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery)) {
                                    updateStatement.executeUpdate();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            updateTimeLabel(waktu);
                        }
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reducing time: " + e.getMessage());
        }
    }


    private void stopExecutor() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown(); // Menghentikan executor
        }
    }



    private void updateTimeLabel(int waktu) {
        timeLabel.setText("Time: " + waktu);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Tambahan untuk memanggil stop method saat aplikasi ditutup
    public void stop(ActionEvent event) {
        try {
            if (connectDB != null) {
                connectDB.close();
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
        }
    }
}
