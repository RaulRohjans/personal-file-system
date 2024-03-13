package com.rohjans.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.rohjans.utils.Helpers;

import java.io.IOException;

/**
 * This is a class that represents the view controller for the TextPopupView.
 *
 * @author Raul Rohjans 202100518
 */
public class TextPopupController {
    /**
     * Root component of the view.
     */
    @FXML
    private Parent popupRoot;

    /**
     * Main text field component.
     */
    @FXML
    private TextField txtText;

    /**
     * Label of the main text field.
     */
    @FXML
    private Label lblText;

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
    public static TextPopupController create() throws IOException {
        FXMLLoader loader = Helpers.getLoadedFXML("text-popup");
        loader.load();
        return loader.getController();
    }

    /**
     * This method shows the view on the main stage.
     *
     * @param title Title to be set on the view.
     * @param label Label to be set above the main text field.
     * @return Content of the text field.
     */
    public String show(String title, String label) {
        return show(title, label, "");
    }

    /**
     * This method shows the view on the main stage.
     *
     * @param title Title to be set on the view.
     * @param label Label to be set above the main text field.
     * @param txtDefaultValue Default value for the main text field.
     * @return Content of the text field.
     */
    public String show(String title, String label, String txtDefaultValue) {
        lblText.setText(label);
        txtText.setText(txtDefaultValue);

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(title);
        stage.setScene(new Scene(popupRoot));
        stage.setResizable(false);
        stage.showAndWait();
        return txtText.getText();
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
        if(txtText.getText().isEmpty()) {
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
        txtText.setText("");
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
