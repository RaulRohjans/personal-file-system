package com.rohjans.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.rohjans.utils.Helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that represents the view controller for the NumberRangeSelectorView.
 *
 * @author Raul Rohjans 202100518
 */
public class NumberRangeSelectorController {
    /**
     * Root component of the view.
     */
    @FXML
    private Parent numSelectorRoot;

    /**
     * From and to text fields.
     */
    @FXML
    private TextField txtFrom, txtTo;

    /**
     * Response array with the selected range (it will always have length 2).
     */
    private ArrayList<Integer> nums;


    /*
     * Methods
     * */
    /**
     * This method creates an instance of the view and loads the controller.
     *
     * @return Instance of the controller.
     * @throws IOException Could happen if the FXML isn't found, but it's unlikely.
     */
    public static NumberRangeSelectorController create() throws IOException {
        FXMLLoader loader = Helpers.getLoadedFXML("number-range-selector");
        loader.load();
        return loader.getController();
    }

    /**
     * This method shows the view on the main stage.
     *
     * @return List with length 2, first index is the "from" and second is the "to".
     */
    public List<Integer> show() {
        nums = new ArrayList<>();
        txtFrom.setText("0");
        txtTo.setText("0");

        /* Set listeners to only allow numbers */
        txtFrom.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.matches("\\d*"))
                    txtFrom.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        txtTo.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.matches("\\d*"))
                    txtTo.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        /* --------------------------------- */

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Number Range Chooser");
        stage.setScene(new Scene(numSelectorRoot));
        stage.setResizable(false);
        stage.showAndWait();

        return nums;
    }

    /**
     * This method closes the view from the main stage.
     */
    private void closePopup() {
        ((Stage) numSelectorRoot.getScene().getWindow()).close();
    }


    /*
     * UI Events
     * */

    /**
     * Listener for the onCancel event which is triggered when the cancel button is pressed.
     */
    @FXML
    public void onCancel() {
        nums = null;

        closePopup();
    }

    /**
     * Listener for the onOk event which is triggered when the ok button is pressed.
     */
    @FXML
    public void onOk() {
        nums.add(Integer.valueOf(txtFrom.getText()));
        nums.add(Integer.valueOf(txtTo.getText()));

        closePopup();
    }
}
