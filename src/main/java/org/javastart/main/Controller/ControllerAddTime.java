package org.javastart.main.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.javastart.main.Database.DbConnect;
import org.javastart.main.Database.ModelUser;

import java.net.URL;

import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerAddTime implements Initializable {
    @FXML
    private TableView<ModelUser> tableView;
    @FXML
    private TableColumn<ModelUser,String>idUsernameTableColumn;
    @FXML
    private TableColumn<ModelUser,Integer>idWaktuTableColumn;
    @FXML
    private TextField tf_search;
    @FXML
    private Button btn_apply;
    @FXML
    private Button btn_cancel;
    @FXML
    private TextField tf_time;

    ObservableList<ModelUser>observableList= FXCollections.observableArrayList();
    @Override

    //function to see table username and filtering.
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DbConnect connection = new DbConnect();
        Connection connectDB= connection.getDbConnection();

        String QueryView="Select usernames,waktu from user";
        try{
            Statement statement= connectDB.createStatement();
            ResultSet queryOutput= statement.executeQuery(QueryView);

            while(queryOutput.next()){
                String QueryUsername=queryOutput.getString("usernames");
                Integer QueryWaktu=queryOutput.getInt("waktu");

                observableList.add(new ModelUser(QueryUsername,QueryWaktu));
            }

            idUsernameTableColumn.setCellValueFactory(new PropertyValueFactory<>("Username"));
            idWaktuTableColumn.setCellValueFactory(new PropertyValueFactory<>("Waktu"));
            tableView.setItems(observableList);

            //Comparator for filtering search
            FilteredList<ModelUser>filterData=new FilteredList<>(observableList, b -> true);
            tf_search.textProperty().addListener((observable, oldValue, newValue)->{
                filterData.setPredicate(ModelUser -> {
                    if (newValue.isEmpty()||newValue.isBlank()||newValue==null){
                        return true;
                    }

                    String searchKeyword= newValue.toLowerCase();
                    if (ModelUser.getUsername().toLowerCase().indexOf(searchKeyword)> -1){
                        return true;
                    }else{
                        return false;
                    }
                });
            });

            SortedList<ModelUser> sortedData= new SortedList<>(filterData);
            sortedData.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedData);

        }catch (SQLException e){
            Logger.getLogger(ModelUser.class.getName()).log(Level.SEVERE,null,e);
            e.printStackTrace();
        }

    }
    public void buttonCancelAction(ActionEvent event){
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void buttonAddTimeAction(ActionEvent event) {
        DbConnect connectDb = new DbConnect();
        try (Connection connectDB = connectDb.getDbConnection()) {
            String Query = "UPDATE user SET waktu = waktu + (?*3600) WHERE usernames = ?";

            // Validate time input as integer
            String timeInput = tf_time.getText();
            try {
                int timeValue = Integer.parseInt(timeInput);
            } catch (NumberFormatException e) {
                // Display alert dialog (consider using a UI library)
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Invalid Time Format");
                alert.setContentText("Please enter an integer value for time.");
                alert.showAndWait();
                return; // Exit the method if validation fails
            }

            try (PreparedStatement st = connectDB.prepareStatement(Query)) {
                st.setString(2, tf_search.getText());
                st.setInt(1, Integer.parseInt(tf_time.getText()));
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

}