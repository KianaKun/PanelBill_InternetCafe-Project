package org.javastart.main.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.javastart.main.Database.DbConnect;
import org.javastart.main.Database.ModelUser;

import java.net.URL;

import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerDeleteUser implements Initializable {
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

    ObservableList<ModelUser>observableList= FXCollections.observableArrayList();
    @Override
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

    //function to close popup window.
    public void buttonCancelAction(ActionEvent event){
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    //function to deleting user from database.
    public void delUser(ActionEvent event){
        DbConnect connection = new DbConnect();
        try (Connection connectDB = connection.getDbConnection()) {
            String Query = "delete from user where usernames=?";
            try (PreparedStatement st = connectDB.prepareStatement(Query)) {
                st.setString(1,tf_search.getText());
                st.executeUpdate();
                Node sources=(Node) event.getSource();
                Stage stage=(Stage) sources.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
            System.err.println("Gagal menghapus data pengguna: " + e.getMessage());
            }
        } catch (SQLException e) {
        System.err.println("Gagal terhubung ke database: " + e.getMessage());
        }
    }
}