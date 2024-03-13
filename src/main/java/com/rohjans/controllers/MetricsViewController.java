package com.rohjans.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.rohjans.models.File.FileDTO;
import com.rohjans.models.ItemDTO;
import com.rohjans.utils.Helpers;

import java.io.IOException;

/**
 * This is a class that represents the view controller for the MetricsView.
 *
 * @author Raul Rohjans 202100518
 */
public class MetricsViewController {
    /**
     * Root component of the view.
     */
    @FXML
    private Parent metricsViewRoot;

    /**
     * Storage pie chart component.
     */
    @FXML
    private PieChart metricsChart;


    /*
     * Methods
     * */

    /**
     * This method creates an instance of the view and loads the controller.
     *
     * @return Instance of the controller.
     * @throws IOException Could happen if the FXML isn't found, but it's unlikely.
     */
    public static MetricsViewController create() throws IOException {
        FXMLLoader loader = Helpers.getLoadedFXML("metrics-view");
        loader.load();
        return loader.getController();
    }

    /**
     * This method shows the view on the main stage.
     *
     * @param treeRoot Root item of the PFS tree view.
     * @param selectedDirectory Selected tree item to calculate the chart based on.
     */
    public void show(TreeItem<ItemDTO> treeRoot, TreeItem<ItemDTO> selectedDirectory) {
        /* --- Pie Chart Loading --- */
        long totalSize = calcDirSize(treeRoot); //Get total PFS size
        long selectionSize = calcDirSize(selectedDirectory); //Get selection size

        //Build data object
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList(
                new PieChart.Data(selectedDirectory.getValue().getName(), selectionSize),
                new PieChart.Data("Others", totalSize - selectionSize)
        );

        //Add data to pie chart
        metricsChart.setData(chartData);
        metricsChart.setTitle("Space taken up by selected folder");
        /* -------------------------- */

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Storage Metrics");
        stage.setScene(new Scene(metricsViewRoot));
        stage.setResizable(false);
        stage.showAndWait();
    }

    /**
     * This method that calculates the size of a given directory.
     *
     * @param dir Directory to calculate the size.
     * @return Total size of the directory
     */
    private long calcDirSize(TreeItem<ItemDTO> dir) {
        long size = 0;

        //If it's a file, add size and return (since files don't have children)
        if(dir.getValue() instanceof FileDTO file) return file.getLongFileSize();

        for(TreeItem<ItemDTO> child: dir.getChildren())
            size += calcDirSize(child);

        return size;
    }


    /*
     * UI Events
     * */

    /**
     * Listener for the onOk event which is triggered when the ok button is pressed.
     */
    @FXML
    public void onOk() {
        ((Stage) metricsViewRoot.getScene().getWindow()).close();
    }
}
