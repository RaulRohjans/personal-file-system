package com.rohjans.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.rohjans.utils.Helpers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that represents the view controller for the DateRangeSelectorView.
 *
 * @author Raul Rohjans 202100518
 */
public class DateRangeSelectorController {
    /**
     * Root component of the view.
     */
    @FXML
    private Parent dateSelectorRoot;

    /**
     * From and To date picker components
     */
    @FXML
    private DatePicker dtpFrom, dtpTo;

    /**
     * Array that returns the from and to dates (length is always 2)
     */
    private ArrayList<LocalDate> dates;


    /*
     * Methods
     * */

    /**
     * This method creates an instance of the view and loads the controller.
     *
     * @return Instance of the controller.
     * @throws IOException Could happen if the FXML isn't found, but it's unlikely.
     */
    public static DateRangeSelectorController create() throws IOException {
        FXMLLoader loader = Helpers.getLoadedFXML("date-range-selector");
        loader.load();
        return loader.getController();
    }

    /**
     * This method shows the view on the main stage.
     *
     * @return List containing the "from" and "to" dates (index 0 and 1).
     */
    public List<LocalDate> show() {
        dates = new ArrayList<>();
        dtpFrom = new DatePicker(LocalDate.now());
        dtpTo = new DatePicker(LocalDate.now());

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Date Range Chooser");
        stage.setScene(new Scene(dateSelectorRoot));
        stage.setResizable(false);
        stage.showAndWait();

        return dates;
    }

    /**
     * This method closes the view from the main stage.
     */
    private void closePopup() {
        ((Stage) dateSelectorRoot.getScene().getWindow()).close();
    }


    /*
     * UI Events
     * */

    /**
     * Listener for the onCancel event which is triggered when the cancel button is pressed.
     */
    @FXML
    public void onCancel() {
        dates = null;

        closePopup();
    }

    /**
     * Listener for the onOk event which is triggered when the ok button is pressed.
     */
    @FXML
    public void onOk() {
        dates.add(dtpFrom.getValue());
        dates.add(dtpTo.getValue());

        closePopup();
    }
}
