package com.rohjans.controllers;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jooq.DSLContext;
import com.rohjans.models.File.FileDTO;
import com.rohjans.models.File.FileMapper;
import com.rohjans.models.File.FileRepository;
import com.rohjans.models.Folder.FolderDTO;
import com.rohjans.models.Folder.FolderMapper;
import com.rohjans.models.Folder.FolderRepository;
import com.rohjans.models.ItemDTO;
import com.rohjans.ui.IconPanel;
import com.rohjans.utils.DBEngine;
import com.rohjans.utils.Helpers;
import com.rohjans.utils.PasswordManager;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This is a class that represents the view controller for the PFSController, in other words, the main app controller.
 * <p>
 * NOTE: This class is freaking huge! It should be refactored, the scenes can be separated into different parts
 * which would spread the code across different controllers.
 * A publisher-subscriber pattern could also be used instead of the MVC one, but this goes against the rules of the
 * project :( .
 *
 * @author Raul Rohjans 202100518
 */
public class PFSController implements Initializable {
    /**
     * Root component of the view.
     */
    @FXML
    private Parent mainViewRoot;

    /**
     * Main tree view component, where all the files and folders are displayed.
     */
    @FXML
    private TreeView<ItemDTO> pfsTreeView;

    /**
     * Labels that make up the item Metadata tab.
     */
    @FXML
    private Label lblInfoCreated, lblInfoLastEdited, lblInfoChangeCounter, lblInfoIsLocked,
            lblInfoSize, lblInfoImportance;

    /**
     * Metadata file info specifics container.
     */
    @FXML
    private Parent infoFileContainer;

    /**
     * Metadata file content container.
     */
    @FXML
    private TabPane itemInfoTabContainer;

    /**
     * Context menu strip options.
     */
    @FXML
    private Menu confirmMoveOption, cancelMoveOption, ItemOption, SearchOption, MetricsOption, StorageOption;

    /**
     * File content text area component.
     */
    @FXML
    private TextArea txtFileContent;

    /* ---- Database ---- */
    /**
     * Database file repository instance.
     */
    private FileRepository fileRepository;

    /**
     * Database folder repository instance.
     */
    private FolderRepository folderRepository;
    /* ------------------ */

    /* --- Tree Stuff --- */
    /**
     * PFS tree hidden root item.
     */
    private TreeItem<ItemDTO> treeRootItem;
    /* ------------------------- */

    /* --- Move Operations --- */
    /**
     * Temporary storage location for the file being moved in a PFS move operation.
     */
    private TreeItem<ItemDTO> tempMoveItem;
    /* ----------------------- */

    /* --- File Authentication --- */
    /**
     * Temporary array of authenticated files.
     * <p>
     * NOTE:
     * So this one is quite an ugly fix...
     * If an item is locked, and we select it in the tree view it asks for a password prompt.
     * The issue starts when we have components that reselect the same item upon interacting with it, like editing
     * the file content, for example. It's also quite annoying having to reauthenticate the same file if the user just
     * switches the selection to a different folder for a second.
     * So hence why we use this list, it stores the last authenticated file, and it is cleared when selecting a
     * different file, so its length is no longer than 1.
     */
    private ArrayList<UUID> authenticatedFiles;
    /* --------------------------- */

    /**
     * This method is an implementation of the initialize interface, and it initializes the tree by
     * loading the database repositories, hiding menu items that are hidden by default and fills the tree view data.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initialize();
    }

    /**
     * This method is an implementation of the initialize interface, and it initializes the tree by
     * loading the database repositories, hiding menu items that are hidden by default and fills the tree view data.
     */
    public void initialize() {
        //Hide move options by default
        toggleMoveOptions(false);

        try { //Start DB context
            DSLContext ctx = DBEngine.getDslContext();
            fileRepository = new FileRepository(ctx);
            folderRepository = new FolderRepository(ctx);
        }
        catch (SQLException e) {
            Helpers.showErrorDialog(e);
        }

        //Hide File Info specifics on the metadata panel
        //as well as the content tab
        infoFileContainer.setVisible(false);
        itemInfoTabContainer.getTabs().get(1).getContent().setVisible(false);

        //Instance authenticated items
        authenticatedFiles = new ArrayList<>();

        //Load file and folder tree to UI
        loadTree();
    }


    /*
    * Internal methods
    * */
    /**
     * This method loads the PFS tree view with data from the database repositories.
     */
    private void loadTree() {
        //Start TreeView instance
        instanceTreeView();

        //Fetch folders and sort them into a Map, so we don't need to constantly
        //search the list for a given id, as this can be resource heavy
        List<FolderDTO> folders = FolderMapper.toFolderDTO(folderRepository.findAll());
        Map<UUID, TreeItem<ItemDTO>> folderItems = new HashMap<>();

        for (FolderDTO folder : folders) {
            TreeItem<ItemDTO> folderItem = createTreeItem(folder);
            folderItems.put(folder.getId(), folderItem);

            if (folder.getParent() == null) { // It's a root folder
                treeRootItem.getChildren().add(folderItem);
            } else {
                TreeItem<ItemDTO> parentItem = folderItems.get(folder.getParent());
                if (parentItem != null) parentItem.getChildren().add(folderItem);
            }
        }

        List<FileDTO> files = FileMapper.toFileDTO(fileRepository.findAll());
        for (FileDTO file : files) {
            TreeItem<ItemDTO> fileItem = createTreeItem(file);
            TreeItem<ItemDTO> parentFolderItem = folderItems.get(file.getParent());

            if (parentFolderItem != null) parentFolderItem.getChildren().add(fileItem);
            else treeRootItem.getChildren().add(fileItem); //File belongs to root
        }

        treeRootItem.setExpanded(true);
    }

    /**
     * This method shows and/or hides context menu items.
     *
     * @param show Whether to show or hide the default items.
     */
    private void toggleMoveOptions(boolean show) {
        confirmMoveOption.setVisible(show);
        cancelMoveOption.setVisible(show);

        ItemOption.setVisible(!show);
        SearchOption.setVisible(!show);
        MetricsOption.setVisible(!show);
        StorageOption.setVisible(!show);
    }

    /**
     * This method instances the tree view component by setting a root item and listeners for item selection
     * and double click.
     */
    private void instanceTreeView() {
        // Start tree instance and assign root item
        FolderDTO rootFolder = new FolderDTO(null, "root");
        rootFolder.setId(null);

        treeRootItem = new TreeItem<>(rootFolder);
        pfsTreeView.setRoot(treeRootItem);
        pfsTreeView.setShowRoot(false);

        //Add listener to handle Tree Item Selection, which fill data on the panels
        pfsTreeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == null) return;

                    //If the selection is the same, we clear it
                    handleNewTreeItemSelection(newValue);
                }
        );

        //Add listener to handle tree item clicks
        pfsTreeView.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                //Open file with default program on double click
                if(mouseEvent.getClickCount() == 2)
                {
                    TreeItem<ItemDTO> item = getSelectedItem();

                    if(!(item.getValue() instanceof FileDTO)) return;

                    Helpers.showDialogMessage("You just made this item feel special :)");
                }
            }
        });
    }

    /**
     * This method clears all the data from the metadata panel.
     */
    private void clearMetadata() {
        txtFileContent.setText("");
        lblInfoIsLocked.setText("");
        lblInfoSize.setText("");
        lblInfoImportance.setText("");
        txtFileContent.setText("");
        lblInfoCreated.setText("");
        lblInfoLastEdited.setText("");
        lblInfoChangeCounter.setText("");
    }

    /**
     * This method handles all the logic when a new tree item is selected.
     *
     * @param item The item that was selected.
     */
    private void handleNewTreeItemSelection(TreeItem<ItemDTO> item) {
        if(item == null) {
            pfsTreeView.getSelectionModel().clearSelection();
            clearMetadata();
            return;
        }

        ItemDTO itemDTO = item.getValue();

        if(itemDTO instanceof FileDTO file) {

            /*
             * authenticatedFiles will contain the last locked file that was authenticated
             * if the current selection wasn't authenticated before,
             * we clear the list, ask for password, and add the current one
             *
             * The reason we need this is so the prompt isn't constantly asked for with
             * every single interaction the user does outside the TreeView (like saving file contents)
             * */
            if(!authenticatedFiles.contains(file.getId())) {
                authenticatedFiles.clear();

                if(file.isLocked()) { //Ask for auth if file is locked
                    String res = "";
                    try { res = authenticateLockedFile(file.getPassword()); }
                    catch (IOException e) { Helpers.showErrorDialog(e); }
                    if(res == null || res.isEmpty()) {
                        clearMetadata();
                        return;
                    }

                    authenticatedFiles.add(file.getId());
                }
            }

            //Update file info on the metadata panel
            infoFileContainer.setVisible(true);

            lblInfoIsLocked.setText(file.isLocked() ? "Yes" : "No");
            lblInfoSize.setText(String.valueOf(file.getFilesize()));
            lblInfoImportance.setText(String.valueOf(file.getImportance()));

            //Load file content into the metadata panel if the extension is csv or txt
            if(Objects.equals(file.getExtension().toLowerCase(), "txt") ||
                Objects.equals(file.getExtension().toLowerCase(), "csv")) {
                itemInfoTabContainer.getTabs().get(1).getContent().setVisible(true);
                txtFileContent.setText(file.getContent());
            }
        }
        else {
            infoFileContainer.setVisible(false);
            itemInfoTabContainer.getTabs().get(1).getContent().setVisible(false);

        }

        String createdDateStr = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                .format(itemDTO.getCreated()).replace('T', ' ');
        String changedDateStr = itemDTO.getChanged() != null ? DateTimeFormatter.ISO_LOCAL_DATE_TIME
                .format(itemDTO.getChanged()).replace('T', ' ') : "";

        lblInfoCreated.setText(createdDateStr);
        lblInfoLastEdited.setText(changedDateStr);
        lblInfoChangeCounter.setText(String.valueOf(itemDTO.getChangeCounter()));
    }

    /**
     * This method checks if an items name is duplicated within the same level.
     * <p>
     * Item names cannot be the same only on the same tree level, in other words, when in the same folder.
     *
     * @param item Item to check name duplication.
     * @return True if an item with the same name exists, otherwise false.
     */
    private boolean isNameDuplicated(TreeItem<ItemDTO> item) {
        return isNameDuplicated(item.getValue(), item.getParent());
    }

    /**
     * This method checks if an items name is duplicated within the same level.
     * <p>
     * Item names cannot be the same only on the same tree level, in other words, when in the same folder.
     *
     * @param item Item to check name duplication.
     * @param parent Parent to check the duplication on.
     * @return True if an item with the same name exists, otherwise false.
     */
    private boolean isNameDuplicated(ItemDTO item, TreeItem<ItemDTO> parent) {
        /*
        * We only want to check if it's duplicated in the same level, since
        * this is where we don't want names to be the same
        * */

        for(TreeItem<ItemDTO> tItem : parent.getChildren()) {
            if(tItem.getValue() == item) continue; //Ignore current item

            if(Objects.equals(item.getName(), tItem.getValue().getName())) return true;
        }

        return false;
    }

    /**
     * This method authenticates a lock file, allowing access to its contents if the password is correct.
     *
     * @param hashedPassword Saved hashed password for the locked file.
     * @return Plain text password for the file, or null if the password is wrong or operation was cancelled.
     * @throws IOException It would happen while trying to open the password prompt view, but it's very unlikely.
     */
    private String authenticateLockedFile(String hashedPassword) throws IOException {
        boolean showRepeat = (hashedPassword == null || hashedPassword.isEmpty());
        PasswordPromptController pwPromptController = PasswordPromptController.create();

        // First off, unlock file to proceed with operation
        String inputPassword = pwPromptController.show(showRepeat);

        if(inputPassword.isEmpty()){
            Helpers.showErrorDialog("The file cannot be locked/unlocked without a password!");
            return null;
        }

        if(!showRepeat && !PasswordManager.checkPasswordWithHash(inputPassword, hashedPassword)) {
            Helpers.showErrorDialog("Wrong password!");
            return null;
        }

        return inputPassword;
    }

    /**
     * This method creates a new tree node on the PFS tree view, and sets the corresponding listeners.
     *
     * @param itemDTO Item data object to add to the tree.
     * @return The tree node item.
     */
    private TreeItem<ItemDTO> createTreeItem(ItemDTO itemDTO) {
        Node img;
        if(itemDTO instanceof FileDTO) img = new IconPanel("file");
        else img = new IconPanel("folder");

        TreeItem<ItemDTO> treeItem = new TreeItem<>(itemDTO, img);

        /*
        * Create a listener for the item being created on the name property
        * so that when the item gets renamed, the UI Tree gets updated accordingly
        * */
        ChangeListener<String> nameListener = (obs, oldName, newName) -> {
            if(newName.isEmpty()) return;

            TreeItem.TreeModificationEvent<ItemDTO> event = new TreeItem.TreeModificationEvent<>(TreeItem.valueChangedEvent(), treeItem);

            ItemDTO currentItem = event.getTreeItem().getValue();
            currentItem.setName(newName);
            event.getTreeItem().setValue(currentItem);

            Event.fireEvent(treeItem, event);
        };
        itemDTO.nameProperty().addListener(nameListener);

        /*
        * In case the item is removed from the tree or is replaced with another
        * instance of an ItemDTO, we need to either refresh the listener
        * or remove it completely to avoid memory leaks
        * */
        treeItem.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.nameProperty().removeListener(nameListener);
            }
            if (newValue != null) {
                newValue.nameProperty().addListener(nameListener);
            }
        });
        return treeItem;
    }

    /**
     * This method gets the current selection on the PFS tree view.
     *
     * @return The item that is currently select, or the root item if there is no selection.
     */
    private TreeItem<ItemDTO> getSelectedItem(){
        TreeItem<ItemDTO> selectedItem = pfsTreeView.getSelectionModel().getSelectedItem();

        //If nothing is selected, assume the root directory
        if(selectedItem == null)
            selectedItem = treeRootItem;

        return selectedItem;
    }

    /**
     * This method gets the current folder there the user selection is placed at.
     *
     * @return If the current selection is a folder it just returns it, otherwise, if it's a file, it gets the parent.
     */
    private TreeItem<ItemDTO> getCurrentPlacement() {
        TreeItem<ItemDTO> selectedItem = getSelectedItem();

        if(selectedItem.getValue() instanceof FolderDTO) return selectedItem;
        else
            if(selectedItem.getValue().getParent() == null) return treeRootItem;
            else return selectedItem.getParent();
    }

    /**
     * This method holds all the logic for a new tree item creation.
     *
     * @throws IOException May come from the item creation forms for not finding the FXML, but it's unlikely to happen.
     */
    private void createNewItem() throws IOException {
        TreeItem<ItemDTO> selectedItem = getCurrentPlacement();

        ButtonType btnFile = new ButtonType("File", ButtonBar.ButtonData.OTHER);
        ButtonType btnFolder = new ButtonType("Folder", ButtonBar.ButtonData.YES);
        ButtonType btnCancelled = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> res = Helpers.showChooserPopup("Choose Item Type",
                "Would you like to create a file or folder?", "",
                Alert.AlertType.CONFIRMATION, btnCancelled, btnFile, btnFolder);

        if(res.orElse(btnCancelled) == btnCancelled) return; //User cancelled operation

        //Name the new item
        TreeItem<ItemDTO> treeItem;
        boolean isDuped;
        do {
            /* Get filename from user */
            String itemName;
            int importance = -1;
            if(res.orElse(btnFolder) == btnFile) {
                FileCreationController fileCreationController = FileCreationController.create();
                List<String> data = fileCreationController.show();
                if(data == null) return; //User cancelled operation

                itemName = data.get(0);
                importance = Integer.parseInt(data.get(1));
            }
            else {
                TextPopupController textPopupController = TextPopupController.create();
                itemName = textPopupController.show("Create New Folder", "Folder name:");
            }

            if(itemName.isEmpty()) return; //User canceled operation
            /* ---------------------- */

            ItemDTO item;
            if(res.orElse(btnFolder) == btnFile) {
                item = new FileDTO(selectedItem.getValue().getId(), itemName, Helpers.parseFileExtension(itemName));
                ((FileDTO) item).setImportance(importance);
            }
            else item = new FolderDTO(selectedItem.getValue().getId(), itemName);

            treeItem = createTreeItem(item);
            isDuped = isNameDuplicated(treeItem.getValue(), selectedItem);

            if(isDuped) Helpers.showErrorDialog("The item's name is duplicated with one on the same level");
        } while (isDuped);

        //Add it to the tree
        selectedItem.getChildren().add(treeItem);
        selectedItem.setExpanded(true); //Expand parent

        //Set new item as selected
        pfsTreeView.getSelectionModel().select(treeItem);

        //Create db record
        if(treeItem.getValue() instanceof FileDTO) fileRepository.create((FileDTO) treeItem.getValue());
        else folderRepository.create((FolderDTO) treeItem.getValue());
    }

    /**
     * This method holds all the item rename logic.
     *
     * @throws IOException May come from the item creation forms for not finding the FXML, but it's unlikely to happen.
     */
    private void renameItem() throws IOException {
        TreeItem<ItemDTO> selectedItem = getSelectedItem();
        boolean isFile = selectedItem.getValue() instanceof FileDTO;

        //If there is no option selected, show error and cancel operation
        if(selectedItem == treeRootItem) {
            Helpers.showErrorDialog("Please select an item first!");
            return;
        }

        /* Get filename from user */
        boolean isDuped = false;
        String itemName;
        do {
            TextPopupController textPopupController = TextPopupController.create();

            if(isFile)
                itemName = textPopupController.show("Rename File", "File name:",
                        selectedItem.getValue().getName());
            else
                itemName = textPopupController.show("Rename Folder", "Folder name:",
                        selectedItem.getValue().getName());

            if(itemName.isEmpty()) return; //User canceled operation

            /*
            * Change the name on a temporary clone, we do this so the changes
            * are not "real" until we know the name is validated
            * */
            ItemDTO tempClone = selectedItem.getValue().clone();
            tempClone.setName(itemName);

            isDuped = isNameDuplicated(tempClone, selectedItem.getParent());

            if(isDuped) Helpers.showErrorDialog("The item's name is duplicated with one on the same level");
        } while(isDuped);
        /* ---------------------- */

        //Update the name with custom setter
        selectedItem.getValue().setName(itemName);
        selectedItem.getValue().incrementCounter();
        selectedItem.getValue().updateChanged();

        //Update db record
        if(isFile) fileRepository.update((FileDTO) selectedItem.getValue());
        else folderRepository.update((FolderDTO) selectedItem.getValue());

        //Update metadata
        handleNewTreeItemSelection(selectedItem);
    }

    /**
     * This method holds all the item removal logic.
     */
    private void removeItem() {
        TreeItem<ItemDTO> selectedItem = getSelectedItem();
        boolean isFile = selectedItem.getValue() instanceof FileDTO;

        //If there is no option selected, show error and cancel operation
        if(selectedItem == treeRootItem) {
            Helpers.showErrorDialog("Please select an item first!");
            return;
        }

        boolean res = Helpers.showChooserPopup("Remove Item", "Are you sure you want to remove this item?",
            Alert.AlertType.WARNING);

        if(!res) return; //User cancelled operation

        //Remove item from db
        if(isFile) fileRepository.removeById(selectedItem.getValue().getId());
        else folderRepository.removeById(selectedItem.getValue().getId());

        //Remove item from tree
        selectedItem.getParent().getChildren().remove(selectedItem);
    }

    /**
     * This method holds all the item locking and unlocking logic.
     *
     * @throws IOException May come from the instanced views for not finding the FXML, but it's unlikely to happen.
     */
    private void lockUnlockFile() throws IOException {
        TreeItem<ItemDTO> item = getSelectedItem();

        if(!(item.getValue() instanceof FileDTO)) {
            Helpers.showErrorDialog("Only files can be locked or unlocked");
            return;
        }
        FileDTO file = (FileDTO) item.getValue();

        String currentPwHash = file.getPassword();
        if(currentPwHash == null) currentPwHash = "";

        if(file.isLocked()) {
            String res = authenticateLockedFile(currentPwHash);
            if(res == null || res.isEmpty()) return;

            //Now unlock the file
            file.setLocked(false);

            //Update db record
            fileRepository.update(file);

            //Update metadata
            item.setValue(file);
            handleNewTreeItemSelection(item);

            return;
        }

        if(!currentPwHash.isEmpty()) { //Let the user choose to keep using the old password or input new one
            /*
            * We have to declare the buttons here since we need to know if the user left he popup, or choose yes or no
            * */
            ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.OTHER);
            ButtonType btnCancelled = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            Optional<ButtonType> res = Helpers.showChooserPopup("Keep Password",
                    "Looks like you already had a password for this file, would you like to keep using it?",
                    "", Alert.AlertType.WARNING, btnCancelled, btnYes, btnNo);

            if(res.orElse(btnCancelled) == btnCancelled) return; //User cancelled operation

            if(res.orElse(btnCancelled) == btnYes) { //If so, authenticate
                String inputPassword = authenticateLockedFile(currentPwHash);
                if(inputPassword == null || inputPassword.isEmpty()) return;

                //Update lock state
                file.setLocked(true);

                //Update db record
                fileRepository.update(file);

                //Update metadata
                item.setValue(file);
                handleNewTreeItemSelection(item);

                return;
            }
        }

        //If the user choose to input a new password
        //or there was none for this file, ask for a new one
        String inputPassword = authenticateLockedFile(null);

        //Update lock state
        file.setLocked(true);

        //Update password hash
        file.setPassword(PasswordManager.createPasswordHash(inputPassword));

        //Update db record
        fileRepository.update(file);

        //Update metadata
        item.setValue(file);
        handleNewTreeItemSelection(item);
    }

    /**
     * This method holds all the item duplication logic.
     */
    private void duplicateFile() {
        TreeItem<ItemDTO> item = getSelectedItem();

        if(!(item.getValue() instanceof FileDTO)) {
            Helpers.showErrorDialog("Only files can be duplicated");
            return;
        }

        //Clone current file
        FileDTO dupFile = (FileDTO) item.getValue().clone();

        //Change the name
        renameDuplicatedFile(dupFile, item.getParent());

        //Add it to the same parent in the tree
        TreeItem<ItemDTO> itemParent = getCurrentPlacement();
        itemParent.getChildren().add(createTreeItem(dupFile));

        //Save on DB
        fileRepository.create(dupFile);
    }

    /**
     * This method is a helper that renames a duplicated file.
     *
     * @param item Duplicated item to be renamed.
     * @param parent Parent where the duplicated item is located at.
     */
    private void renameDuplicatedFile(ItemDTO item, TreeItem<ItemDTO> parent) {
        String ogName = item.getName();

        int copyCount = 1;
        while (isNameDuplicated(item, parent)){
            item.setName("(" + copyCount++ + ") " + ogName);

            if(copyCount > 1000) {
                Helpers.showErrorDialog("Too many files in the directory");
                return;
            }
        }
    }

    /**
     * This method holds all the folder flattening logic.
     */
    private void flattenFolder() {
        TreeItem<ItemDTO> item = getSelectedItem();

        if(!(item.getValue() instanceof FolderDTO)) {
            Helpers.showErrorDialog("Only folders can be flattened");
            return;
        }

        boolean res = Helpers.showChooserPopup("Flatten Folder", "Are you sure you want to flatten this folder? ",
                "\nThis will remove all subdirectories and might rename existing files!", Alert.AlertType.WARNING);

        //Operation cancelled, user closed the chooser window without answering
        if(!res) return;

        removeSubFolders(item);
        pfsTreeView.refresh();
    }

    /**
     * This method is a helper that removes all the sub folders withing another folder.
     *
     * @param item Folder to removed sub folders from.
     */
    private void removeSubFolders(TreeItem<ItemDTO> item) { removeSubFolders(item, item); }

    /**
     * This method is a helper that removes all the sub folders withing another folder.
     *
     * @param item Folder to removed sub folders from.
     * @param root Folder root location.
     */
    private void removeSubFolders(TreeItem<ItemDTO> item, TreeItem<ItemDTO> root) {
        ArrayList<TreeItem<ItemDTO>> children = new ArrayList<>(item.getChildren());

        /*
        * This is a recursive approach, go through all the item's children
        * if it's a file move it to the current parent
        * */
        for(TreeItem<ItemDTO> child : children) {
            if(child.getValue() instanceof FileDTO file) {
                //Make sure filename does not make collisions
                renameDuplicatedFile(child.getValue(), root);

                //Move the file to the correct parent
                child.getValue().setParent(root.getValue().getId());
                root.getChildren().add(child);

                //Update DB
                fileRepository.update(file);
            }
            else {
                removeSubFolders(child, root); //Apply recursive logic

                //Remove folder after everything has been moved
                folderRepository.removeById(child.getValue().getId());
            }
            item.getChildren().remove(child);
        }
    }

    /**
     * This method holds all the logic for item moving operations
     */
    private void moveFile() {
        TreeItem<ItemDTO> item = getSelectedItem();

        if(!(item.getValue() instanceof FolderDTO)) {
            Helpers.showErrorDialog("Files can only be moved into folders");
            return;
        }

        if(tempMoveItem == null) {
            Helpers.showErrorDialog("No item selected to move!");
            toggleMoveOptions(false);
            return;
        }

        //Remove from old location
        tempMoveItem.getParent().getChildren().remove(tempMoveItem);

        //Rename file if needed
        renameDuplicatedFile(tempMoveItem.getValue(), item);

        //Move item to new location
        tempMoveItem.getValue().setParent(item.getValue().getId());
        item.getChildren().add(tempMoveItem);

        //Update DB
        fileRepository.update((FileDTO) tempMoveItem.getValue());

        //Set options back to original
        toggleMoveOptions(false);
    }

    /**
     * This method holds all the logic for item searching operations.
     *
     * @param searchParam Parameter that decides on what the search will be based on (name, content, etc.).
     * @throws IOException May come from the instanced views for not finding the FXML, but it's unlikely to happen.
     */
    private void search(Helpers.SearchMode searchParam) throws IOException {
        SearchViewController searchController = SearchViewController.create();
        TreeItem<ItemDTO> item = searchController.show(searchParam, treeRootItem);

        if(item == null) return; //User cancelled

        pfsTreeView.getSelectionModel().select(item);
    }

    /**
     * This method holds all the logic for file content saving.
     */
    private void saveContentFile() {
        TreeItem<ItemDTO> item = getSelectedItem();

        //In theory this should never happen since it is hidden when a folder is selected
        if(!(item.getValue() instanceof FileDTO)) {
            Helpers.showErrorDialog("Only files can have text content!");
            return;
        }

        //Update file content and metadata
        FileDTO file = (FileDTO) item.getValue();
        file.setContent(txtFileContent.getText());
        file.updateChanged();
        file.incrementCounter();
        file.updateFileSize();

        //Refresh metadata panel
        handleNewTreeItemSelection(item);

        //Update DB
        fileRepository.update(file);

        Helpers.showDialogMessage("File content saved successfully!");
    }

    /**
     * This method holds all the logic for item backup operations.
     *
     * @throws SQLException This will happen if the connection to the database fails.
     * @throws IOException This ill will happen if the file cannot be written on the filesystem.
     */
    private void backupDbContent() throws SQLException, IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        //Ask user for backup save location
        File selectedDirectory = directoryChooser.showDialog(mainViewRoot.getScene().getWindow());

        if(selectedDirectory == null) return; //User cancelled operation

        DBEngine.backupDbData(selectedDirectory.getAbsolutePath());

        Helpers.showDialogMessage("Data backed up successfully!");
    }

    /**
     * This method holds all the logic for restoring operations.
     *
     * @throws SQLException This will happen if the connection to the database fails.
     * @throws IOException This ill will happen if the file cannot be written on the filesystem.
     */
    protected void restoreDbContent() throws SQLException, IOException {
        //Ask user to confirm operation
        boolean res = Helpers.showChooserPopup("Restore Data From Backup", "Are you sure you want to proceed with the operation?",
                "This will remove ALL the data currently stored on the Database!", Alert.AlertType.WARNING);
        if(!res) return; //Operation cancelled

        //Ask user for file location
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JSON Files (*.json)",
                "*.json");

        fileChooser.getExtensionFilters().add(filter); //Add file extension filter

        //Ask user for file location
        File backupFile = fileChooser.showOpenDialog(mainViewRoot.getScene().getWindow());
        if(backupFile == null) return; //User cancelled

        DBEngine.restoreDbData(backupFile);

        Helpers.showDialogMessage("Data restored successfully!");

        initialize(); //Reload entire application
    }

    /**
     * This method holds all the logic for item cleansing operations (by date).
     *
     * @throws IOException May come from the instanced views for not finding the FXML, but it's unlikely to happen.
     */
    protected void cleanByDate() throws IOException {
        //Ask user for dates
        DateRangeSelectorController dateSelectorView = DateRangeSelectorController.create();
        List<LocalDate> dates = dateSelectorView.show();
        if(dates == null) return; //Operation cancelled

        //Ask user to confirm operation
        boolean res = Helpers.showChooserPopup("Clean By Date",
                "Are you sure you want to delete all files between these dates?", Alert.AlertType.WARNING);
        if(!res) return; //Operation cancelled

        List<TreeItem<ItemDTO>> files = new ArrayList<>();
        getFilesToDelete(treeRootItem, files, dates.get(0), dates.get(1));

        //Remove the files from the tree and update DB
        for(TreeItem<ItemDTO> delFile : files) {
            delFile.getParent().getChildren().remove(delFile);

            fileRepository.removeById(delFile.getValue().getId());
        }

        Helpers.showDialogMessage("Files deleted successfully!");
    }

    /**
     * This method holds all the logic for item cleansing operations (by importance).
     *
     * @throws IOException May come from the instanced views for not finding the FXML, but it's unlikely to happen.
     */
    protected void cleanByImportance() throws IOException {
        //Ask user for dates
        NumberRangeSelectorController numSelectorView = NumberRangeSelectorController.create();
        List<Integer> nums = numSelectorView.show();
        if(nums == null) return; //Operation cancelled

        //Ask user to confirm operation
        boolean res = Helpers.showChooserPopup("Clean By Importance",
                "Are you sure you want to delete all files between these importance values?", Alert.AlertType.WARNING);
        if(!res) return; //Operation cancelled

        List<TreeItem<ItemDTO>> files = new ArrayList<>();
        getFilesToDelete(treeRootItem, files, nums.get(0), nums.get(1));

        //Remove the files from the tree and update DB
        for(TreeItem<ItemDTO> delFile : files) {
            delFile.getParent().getChildren().remove(delFile);

            fileRepository.removeById(delFile.getValue().getId());
        }

        Helpers.showDialogMessage("Files deleted successfully!");
    }

    /**
     * This method is a helper that gets a list of files to delete from the cleaning operations.
     *
     * @param item Item whose children will be checked for deletion.
     * @param files List of file candidates to remove.
     * @param fromDate Starting date that will be compared against the creation date of the files.
     * @param toDate Ending date that will be compared against the creation date of the files.
     */
    protected void getFilesToDelete(TreeItem<ItemDTO> item, List<TreeItem<ItemDTO>> files, LocalDate fromDate, LocalDate toDate) {
        /*
        * If the file is in between the date range add it to the list
        * */
        if(item.getValue() instanceof FileDTO file) {
            if((file.getCreated().toLocalDate().isAfter(fromDate) &&
                    file.getCreated().toLocalDate().isBefore(toDate)) ||
                    file.getCreated().toLocalDate().isEqual(fromDate) ||
                    file.getCreated().toLocalDate().isEqual(toDate))
                files.add(item);
        }

        for(TreeItem<ItemDTO> child: item.getChildren())
            getFilesToDelete(child, files, fromDate, toDate);
    }

    /**
     * This method is a helper that gets a list of files to delete from the cleaning operations.
     *
     * @param item Item whose children will be checked for deletion.
     * @param files List of file candidates to remove.
     * @param fromNum Starting number that will be compared against the importance of the files.
     * @param toNum Ending number that will be compared against the importance of the files.
     */
    protected void getFilesToDelete(TreeItem<ItemDTO> item, List<TreeItem<ItemDTO>> files, int fromNum, int toNum) {
        /*
         * If the file is in between the date range add it to the list
         * */
        if(item.getValue() instanceof FileDTO file &&
                file.getImportance() >= fromNum && file.getImportance() <= toNum)
            files.add(item);

        for(TreeItem<ItemDTO> child: item.getChildren())
            getFilesToDelete(child, files, fromNum, toNum);
    }

    /**
     * This method holds all the logic for showing the PFS storage metrics.
     *
     * @throws IOException May come from the instanced views for not finding the FXML, but it's unlikely to happen.
     */
    protected void showFolderMetrics() throws IOException {
        TreeItem<ItemDTO> item = getSelectedItem();

        if(!(item.getValue() instanceof FolderDTO)) {
            Helpers.showErrorDialog("Metrics can only be shown on Folders");
            return;
        }

        if(item == treeRootItem) {
            Helpers.showErrorDialog("Please select a folder first!");
            return;
        }

        MetricsViewController metricsViewController = MetricsViewController.create();
        metricsViewController.show(treeRootItem, item);
    }


    /*
    * UI Events
    * */

    /**
     * Listener for the onNewItem event which is triggered when the new item menu option is pressed.
     */
    @FXML
    protected void onNewItem() {
        try { createNewItem(); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for the onRenameItem event which is triggered when the rename item menu option is pressed.
     */
    @FXML
    protected void onRenameItem() {
        try { renameItem(); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for the onDeleteItem event which is triggered when the remove item menu option is pressed.
     */
    @FXML
    protected void onDeleteItem() {
        try { removeItem(); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for the onLockUnlockFile event which is triggered when the lock menu option is pressed.
     */
    @FXML
    protected void onLockUnlockFile() {
        try { lockUnlockFile(); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for the onDuplicateFile event which is triggered when the duplicate item menu option is pressed.
     */
    @FXML
    protected void onDuplicateFile() {
        try { duplicateFile(); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for the onFlattenFolder event which is triggered when the flatten folder menu option is pressed.
     */
    @FXML
    protected void onFlattenFolder() {
        try { flattenFolder(); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for the onMoveFile event which is triggered when the move item menu option is pressed.
     */
    @FXML
    protected void onMoveFile() {
        TreeItem<ItemDTO> item = getSelectedItem();

        if(!(item.getValue() instanceof FileDTO)) {
            Helpers.showErrorDialog("Only files can be moved around");
            return;
        }

        //Save item to be moved
        tempMoveItem = item;

        toggleMoveOptions(true);
    }

    /**
     * Listener for the onConfirmMoveOption event which is triggered when the confirm move menu option is pressed.
     */
    @FXML
    protected void onConfirmMoveOption() {
        moveFile();
    }

    /**
     * Listener for the onCancelMoveOption event which is triggered when the cancel move operation menu option is pressed.
     */
    @FXML
    protected void onCancelMoveOption() {
        tempMoveItem = null;
        toggleMoveOptions(false);
    }

    /**
     * Listener for the onSearchByName event which is triggered when the search item menu option is pressed.
     */
    @FXML
    protected void onSearchByName() {
        try { search(Helpers.SearchMode.NAME); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for the onSearchByContent event which is triggered when the search item menu option is pressed.
     */
    @FXML
    protected void onSearchByContent() {
        try { search(Helpers.SearchMode.CONTENT); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for the onSaveFileContent event which is triggered when the save file content button option is pressed,
     * in the metadata menu, on the content tab.
     */
    @FXML
    protected void onSaveFileContent() {
        try { saveContentFile(); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for all the keybinding triggers on the PFS.
     */
    @FXML
    protected void onKeyRelease(KeyEvent event) {
        //Disable this if the user is writing
        if(txtFileContent.isFocused()) return;

        try {
            switch (event.getCode()) {
                case DELETE -> {
                    removeItem();
                }
                case D -> {
                    duplicateFile();
                }
                case N -> {
                    createNewItem();
                }
                case F2 -> {
                    renameItem();
                }
                case S -> {
                    search(Helpers.SearchMode.NAME);
                }
                case F -> {
                    flattenFolder();
                }
                case L -> {
                    lockUnlockFile();
                }
            }
        }
        catch (Exception e) { Helpers.showErrorDialog(e); }

    }

    /**
     * Listener for the onBackup event which is triggered when the backup menu option is pressed.
     */
    @FXML
    protected void onBackup() {
        try { backupDbContent(); }
        catch (SQLException e) {
            Helpers.showErrorDialog(e, "An error occurred when communicating with the database engine");
        } catch (IOException e) {
            Helpers.showErrorDialog(e, "An error occurred when saving the backup file");
        }
    }

    /**
     * Listener for the onRestore event which is triggered when the restore menu option is pressed.
     */
    @FXML
    protected void onRestore() {
        try { restoreDbContent(); }
        catch (SQLException e) {
            Helpers.showErrorDialog(e, "Error while import data, could not communicate with the DB");
        } catch (IOException e) {
            Helpers.showErrorDialog(e, "Could not parse data from the selected file");
        }

    }

    /**
     * Listener for the onCleanByDate event which is triggered when the clean storage menu option is pressed.
     */
    @FXML
    protected void onCleanByDate() {
        try { cleanByDate(); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for the onCleanByImportance event which is triggered when the clean storage menu option is pressed.
     */
    @FXML
    protected void onCleanByImportance() {
        try { cleanByImportance(); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for the onMetrics event which is triggered when the show storage metrics menu option is pressed.
     */
    @FXML
    protected void onMetrics() {
        try { showFolderMetrics(); }
        catch (Exception e) { Helpers.showErrorDialog(e); }
    }

    /**
     * Listener for the onQuit event which is triggered when the quit button is pressed.
     */
    @FXML
    protected void onQuit() {
        System.exit(0);
    }
}
