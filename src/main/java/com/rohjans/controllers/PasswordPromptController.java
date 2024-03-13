package com.rohjans.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.rohjans.utils.Helpers;

import java.io.IOException;
import java.util.Objects;

/**
 * This is a class that represents the view controller for the PasswordPromptView.
 *
 * @author Raul Rohjans 202100518
 */
public class PasswordPromptController {
    /**
     * Root component of the view.
     */
    @FXML
    private Parent passwordPrompt;

    /**
     * Password text fields.
     */
    @FXML
    private PasswordField txtPassword, txtRepeatPassword;

    /**
     * Repeat password text field label.
     */
    @FXML
    private Label lblRepeatPassword;

    /**
     * Cancel button component.
     */
    @FXML
    private Button btnCancel;


    /*
     * Methods
     * */

    /**
     * This method creates an instance of the view and loads the controller.
     *
     * @return Instance of the controller.
     * @throws IOException Could happen if the FXML isn't found, but it's unlikely.
     */
    public static PasswordPromptController create() throws IOException {
        FXMLLoader loader = Helpers.getLoadedFXML("password-prompt");
        loader.load();
        return loader.getController();
    }

    /**
     * This method shows the view on the main stage.
     *
     * @param showRepeat Whether to show the repeat password section.
     * @return Password
     */
    public String show(boolean showRepeat) {
        if(showRepeat) {
            txtRepeatPassword.setVisible(true);
            lblRepeatPassword.setVisible(true);
        }
        else {
            txtRepeatPassword.setVisible(false);
            lblRepeatPassword.setVisible(false);
        }

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Password Authentication");
        stage.setScene(new Scene(passwordPrompt));
        stage.setResizable(false);
        stage.showAndWait();

        return txtPassword.getText();
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
        if(txtPassword.getText().isEmpty() ||
                (txtRepeatPassword.getText().isEmpty() && txtRepeatPassword.isVisible())) {
            Helpers.showErrorDialog("Please fill in all the fields!");
            return;
        }

        if(!Objects.equals(txtPassword.getText(), txtRepeatPassword.getText()) && txtRepeatPassword.isVisible()) {
            Helpers.showErrorDialog("Please make sure the password match!");
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
        txtPassword.setText("");
        txtRepeatPassword.setText("");

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
