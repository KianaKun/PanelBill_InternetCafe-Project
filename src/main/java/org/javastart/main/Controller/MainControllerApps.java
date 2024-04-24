package org.javastart.main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainControllerApps{
    @FXML
    private Button btn_add;
    @FXML
    private Button btn_delete;
    @FXML
    private Button btn_time;
    @FXML
    private Button btn_loginUser;

    public void changeSceneTimeUser(ActionEvent event){
        try{
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("AddTime.fxml"));
            Parent root1= fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Search User");
            stage.setScene(new Scene(root1));
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //popup new window to add user.
    public void changeSceneAddUser(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddUser.fxml"));
            Parent root2 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Adding User");
            stage.setScene(new Scene(root2));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //popup new window to delete user.
    public void changeSceneDeleteUser(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DeleteUser.fxml"));
            Parent root3 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Delete User");
            stage.setScene(new Scene(root3));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeSceneLoginUser(ActionEvent event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginUser.fxml"));
            Parent root3 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Login User");
            stage.setScene(new Scene(root3));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}