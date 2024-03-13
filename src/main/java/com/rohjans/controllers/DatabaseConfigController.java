package com.rohjans.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.rohjans.models.DbConfig;
import com.rohjans.utils.Helpers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This is a class that represents the view controller for the DatabaseConfigView.
 *
 * @author Raul Rohjans 202100518
 */
public class DatabaseConfigController implements Initializable {
    /**
     * Root component of the view.
     */
    @FXML
    private Parent dbconfigRoot;

    /**
     * Text fields for database hostname, port, username and password.
     */
    @FXML
    private TextField txtHostname, txtPort, txtUsername, txtPassword;

    /**
     * Cancel button component.
     */
    @FXML
    private Button btnCancel;

    /**
     * This method is an implementation of the initialize interface, and it sets the default values on
     * the text fields
     *
     * @param location The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* Set default DB settings */
        txtHostname.setText("localhost");
        txtPort.setText("5432");
        txtUsername.setText("admin");
        txtPassword.setText("1234");
        /* ----------------------- */
    }


    /*
     * Methods
     * */

    /**
     * This method creates an instance of the view and loads the controller.
     *
     * @return Instance of the controller.
     * @throws IOException Could happen if the FXML isn't found, but it's unlikely.
     */
    public static DatabaseConfigController create() throws IOException {
        FXMLLoader loader = Helpers.getLoadedFXML("database-config");
        loader.load();
        return loader.getController();
    }

    /**
     * This method shows the view on the main stage.
     *
     * @return Database configuration object.
     */
    public DbConfig show() {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Database Connection Credentials");
        stage.setScene(new Scene(dbconfigRoot));
        stage.setResizable(false);
        stage.showAndWait();

        return new DbConfig(
            txtHostname.getText(),
            txtPort.getText(),
            txtUsername.getText(),
            txtPassword.getText()
        );
    }

    /**
     * This method closes the view from the main stage.
     */
    private void closePopup() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    /**
     * This method validates the user input and closes the view.
     */
    private void formConfirm() {
        if(txtHostname.getText().isEmpty() || txtPort.getText().isEmpty()
                || txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            Helpers.showErrorDialog("Please fill in all the fields!");
            return;
        }

        closePopup();
    }


    /*
     * UI Events
     * */

    /**
     * Listener for the onCancel event which is triggered when the cancel button is pressed.
     */
    @FXML
    public void onCancel() {
        //Clear data
        txtHostname.setText("");
        txtPort.setText("");
        txtUsername.setText("");
        txtPassword.setText("");

        closePopup();
    }

    /**
     * Listener for the onOk event which is triggered when the ok button is pressed.
     */
    @FXML
    public void onOk() {
        formConfirm();
    }

    /**
     * Listener for the onKeyRelease event which is triggered when any given key is pressed and the view is focused.
     * We only care about the Enter key, to submit the user input.
     */
    @FXML
    public void onKeyRelease(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) formConfirm();
    }
}
