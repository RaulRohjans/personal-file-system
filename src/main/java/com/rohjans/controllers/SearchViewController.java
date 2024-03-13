package com.rohjans.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.rohjans.models.File.FileDTO;
import com.rohjans.models.ItemDTO;
import com.rohjans.utils.Helpers;
import com.rohjans.utils.ListViewItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is a class that represents the view controller for the SearchViewController.
 *
 * @author Raul Rohjans 202100518
 */
public class SearchViewController {
    /**
     * Main list view component where search results are displayed.
     */
    @FXML
    private ListView<ListViewItem> searchResultsListView;

    /**
     * Root component of the view.
     */
    @FXML
    private Parent searchRoot;

    /**
     * Search text field component.
     */
    @FXML
    private TextField txtSearch;

    /**
     * Root of the PFS item tree view.
     */
    private TreeItem<ItemDTO> fileTreeRoot;

    /**
     * Search results list view selected item.
     */
    private TreeItem<ItemDTO> lsvSelectedItem;

    /**
     * Search mode (either by content or by title).
     */
    private Helpers.SearchMode searchParam;


    /*
     * Methods
     * */

    /**
     * This method creates an instance of the view and loads the controller.
     *
     * @return Instance of the controller.
     * @throws IOException Could happen if the FXML isn't found, but it's unlikely.
     */
    public static SearchViewController create() throws IOException {
        FXMLLoader loader = Helpers.getLoadedFXML("search-view");
        loader.load();
        return loader.getController();
    }

    /**
     * This method shows the view on the main stage.
     *
     * @param searchParam Search filter parameter.
     * @param treeRoot Root of the PFS item tree view.
     * @return Searched item result.
     */
    public TreeItem<ItemDTO> show(Helpers.SearchMode searchParam, TreeItem<ItemDTO> treeRoot) {
        fileTreeRoot = treeRoot;
        this.searchParam = searchParam;

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Search Item");
        stage.setScene(new Scene(searchRoot));
        stage.setResizable(false);
        stage.showAndWait();

        return lsvSelectedItem;
    }

    /**
     * This method closes the view from the main stage.
     */
    private void closePopup() {
        ((Stage) searchResultsListView.getScene().getWindow()).close();
    }

    /**
     * This method validates the user input and closes the view.
     */
    private void formConfirm() {
        lsvSelectedItem = searchResultsListView.getSelectionModel().getSelectedItem().getItem();

        closePopup();
    }


    /*
     * UI Events
     * */

    /**
     * Listener for the onSearch event which is triggered when the search button is pressed.
     */
    @FXML
    protected void onSearch() {
        String searchText = txtSearch.getText();

        if(searchText.isEmpty()) return;

        ArrayList<TreeItem<ItemDTO>> candidates = new ArrayList<>();
        getSearchCandidates(fileTreeRoot, searchText, candidates);

        for(TreeItem<ItemDTO> item : candidates)
            searchResultsListView.getItems().add(new ListViewItem(item));
    }

    /**
     * This method gets the items that match the search terms.
     *
     * @param item Item to search within of.
     * @param query The search term query.
     * @param candidates The items that match the search term.
     */
    public void getSearchCandidates(TreeItem<ItemDTO> item, String query, List<TreeItem<ItemDTO>> candidates)
    {
        if (item == null) return;

        if(item.getValue().getName().contains(query) && this.searchParam == Helpers.SearchMode.NAME)
            candidates.add(item);
        else if (this.searchParam == Helpers.SearchMode.CONTENT) {
            if((item.getValue() instanceof FileDTO file) && file.getContent().contains(query))
                candidates.add(item);
        }

        for (TreeItem<ItemDTO> child : Objects.requireNonNull(item).getChildren())
            getSearchCandidates(child, query, candidates);
    }

    /**
     * Listener for the onCancel event which is triggered when the cancel button is pressed.
     */
    @FXML
    protected void onCancel() {
        lsvSelectedItem = null;
        closePopup();
    }

    /**
     * Listener for the onOk event which is triggered when the ok button is pressed.
     */
    @FXML
    protected void onOk() {
        formConfirm();
    }

    /**
     * Listener for the onKeyRelease event which is triggered when any given key is pressed and the view is focused.
     * We only care about the Enter key, to submit the user input.
     */
    @FXML
    protected void onKeyRelease(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) formConfirm();
    }
}
