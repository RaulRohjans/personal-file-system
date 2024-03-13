package com.rohjans.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.rohjans.utils.Helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that represents the view controller for the FileCreationView.
 *
 * @author Raul Rohjans 202100518
 */
public class FileCreationController {
    /**
     * Root component of the view.
     */
    @FXML
    private Parent fileCreationRoot;

    /**
     * Name and importance text fields.
     */
    @FXML
    private TextField txtText, txtImportance;

    /**
     * Data object to be returned, which carries the name and importance of the file being created
     * (length will always be 2).
     */
    List<String> data;


    /*
     * Methods
     * */

    /**
     * This method creates an instance of the view and loads the controller.
     *
     * @return Instance of the controller.
     * @throws IOException Could happen if the FXML isn't found, but it's unlikely.
     */
    public static FileCreationController create() throws IOException {
        FXMLLoader loader = Helpers.getLoadedFXML("file-creation-view");
        loader.load();
        return loader.getController();
    }

    /**
     * This method shows the view on the main stage.
     *
     * @return List of strings whose first index is the name of the file and the second the importance.
     */
    public List<String> show() {
        data = new ArrayList<>();
        txtImportance.setText("0");

        /* Set listeners to only allow numbers */
        txtImportance.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.matches("\\d*"))
                    txtImportance.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        /* ----------------------------------- */

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Create New File");
        stage.setScene(new Scene(fileCreationRoot));
        stage.setResizable(false);
        stage.showAndWait();

        return data;
    }

    /**
     * This method closes the view from the main stage.
     */
    private void closePopup() {
        ((Stage) fileCreationRoot.getScene().getWindow()).close();
    }

    /**
     * This method validates the user input and closes the view.
     */
    private void formConfirm() {
        if(txtText.getText().isEmpty() || txtImportance.getText().isEmpty()) {
            Helpers.showErrorDialog("Please fill in all the fields!");
            return;
        }

        if(Integer.parseInt(txtImportance.getText()) < 0 || Integer.parseInt(txtImportance.getText()) > 4) {
            Helpers.showErrorDialog("The value of importance has to be between 0 and 4!");
            return;
        }

        data.add(txtText.getText());
        data.add(txtImportance.getText());

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
        data = null;
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
