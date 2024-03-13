package com.rohjans.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Window;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/**
 * This is a class that holds multiple helper methods of different categories for different purposes.
 * <p>
 * Everything that doesn't really have a category is put here.
 *
 * @author Raul Rohjans 202100518
 */
public class Helpers {
    /**
     * This is an enumerator created to define which type of search is being made in the PFS.
     *
     * @author Raul Rohjans 202100518
     */
    public enum SearchMode {
        NAME,
        CONTENT
    }

    /**
     * This method is used as a wrapper to load FXML resources for the views.
     *
     * @param resource Resource name to be loaded.
     * @return A loaded resource instance.
     */
    public static FXMLLoader getLoadedFXML(String resource) {
        return new FXMLLoader(Helpers.class.getResource("/views/" + resource + ".fxml"));
    }

    /**
     * This method is used to get the App icon (the favicon).
     *
     * @return Instance of the icon image.
     * @throws FileNotFoundException Happens if the icon image resource doesn't exist, in theory it shouldn't happen.
     */
    public static Image getAppIcon() throws FileNotFoundException {
        return getResourceIcon("favicon");
    }

    /**
     * This method gets any given resource icon image.
     *
     * @param iconName Name of the icon resource to load.
     * @return Instance of the loaded resource image.
     * @throws FileNotFoundException May happen if the image doesn't exist in the icons folder.
     */
    public static Image getResourceIcon(String iconName) throws FileNotFoundException {
        String iconPath = Objects.requireNonNull(Helpers.class.getResource("/icons/" + iconName + ".png")).toExternalForm();
        return new Image(iconPath);
    }

    /**
     * This method is used to display an error dialog.
     *
     * @param e Exception that was thrown by the program.
     */
    public static void showErrorDialog(Throwable e) {
        StringWriter errorMsg = new StringWriter();
        e.printStackTrace(new PrintWriter(errorMsg));

        showErrorDialog(e, errorMsg.toString());
    }

    /**
     * This method is used to display an error dialog.
     *
     * @param e Exception that was thrown by the program.
     * @param msg Custom message to be displayed to the user.
     */
    public static void showErrorDialog(Throwable e, String msg) {
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * This method is used to display an error dialog.
     *
     * @param e Error message to be displayed to the user.
     */
    public static void showErrorDialog(String e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("An Error Occurred");
        alert.setHeaderText(e);
        alert.showAndWait();
    }

    /**
     * This method is used to display an information dialog.
     *
     * @param msg Message to be displayed to the user.
     */
    public static void showDialogMessage(String msg) { showDialogMessage(msg, Alert.AlertType.INFORMATION); }

    /**
     * This method is used to display an information dialog.
     *
     * @param msg Message to be displayed to the user.
     * @param type Type of icon to be used in the message.
     */
    public static void showDialogMessage(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * This method is used to open a popup with a few options to choose with.
     *
     * @param title Title of the popup.
     * @param message Message to be displayed in the header to the user.
     * @param type Type of icon to be used.
     */
    public static boolean showChooserPopup(String title, String message, Alert.AlertType type) {
        return showChooserPopup(title, message, "", type);
    }

    /**
     * This method is used to open a popup with a few options to choose with.
     *
     * @param title Title of the popup.
     * @param message Message to be displayed in the header to the user.
     * @param context Detailed message of what's going on.
     * @param type Type of icon to be used.
     */
    public static boolean showChooserPopup(String title, String message, String context, Alert.AlertType type) {
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);

        Optional<ButtonType> res = showChooserPopup(title, message, context, type, btnYes, btnNo);
        return res.orElse(btnNo) == btnYes;
    }

    /**
     * This method is used to open a popup with a few options to choose with.
     *
     * @param title Title of the popup.
     * @param message Message to be displayed in the header to the user.
     * @param type Type of icon to be used.
     * @param leftBtn Custom button to be displayed on the left side.
     * @param rightBtn Custom button to be displayed on the right side.
     */
    public static Optional<ButtonType> showChooserPopup(String title, String message, Alert.AlertType type, ButtonType leftBtn, ButtonType rightBtn) {
        return showChooserPopup(title, message, "", type, leftBtn, rightBtn);
    }

    /**
     * This method is used to open a popup with a few options to choose with.
     *
     * @param title Title of the popup.
     * @param message Message to be displayed in the header to the user.
     * @param context Detailed message of what's going on.
     * @param type Type of icon to be used.
     * @param leftBtn Custom button to be displayed on the left side.
     * @param rightBtn Custom button to be displayed on the right side
     */
    public static Optional<ButtonType> showChooserPopup(String title, String message, String context, Alert.AlertType type, ButtonType leftBtn, ButtonType rightBtn) {
        Alert alert = new Alert(type, context, leftBtn, rightBtn);
        alert.setTitle(title);
        alert.setHeaderText(message);

        return alert.showAndWait();
    }

    /**
     * This method is used to open a popup with a few options to choose with.
     *
     * @param title Title of the popup.
     * @param message Message to be displayed in the header to the user.
     * @param context Detailed message of what's going on.
     * @param type Type of icon to be used.
     * @param cancelBtn Custom cancel button to be added to the popup.
     * @param leftBtn Custom button to be displayed on the left side.
     * @param rightBtn Custom button to be displayed on the right side.
     */
    public static Optional<ButtonType> showChooserPopup(String title, String message, String context, Alert.AlertType type,
                                                        ButtonType cancelBtn, ButtonType leftBtn, ButtonType rightBtn) {
        Alert alert = new Alert(type, context, leftBtn, rightBtn, cancelBtn);
        alert.setTitle(title);
        alert.setHeaderText(message);

        return alert.showAndWait();
    }

    /**
     * This method gets the file extension of a given file name.
     *
     * @param filename Name of the file to get the extension from.
     * @return The extension of the file if there is any.
     */
    public static String parseFileExtension(String filename) {
        if(filename.isEmpty()) return "";
        if(!filename.contains(".")) return "";

        String[] parts = filename.split("\\.");
        return parts[parts.length - 1];
    }

    /**
     * This method is used to get the filesystem size of a string in bytes.
     *
     * @param text Text to get the size of.
     * @return Size of the text in bytes.
     */
    public static int calculateStringSizeInBytes(String text) {
        if (text == null || text.isEmpty()) return 0;

        /*
        * Java Strings use UTF16 by default, since we are not storing files on the disk
        * we will just assume this is the charset that is being used
        * */
        return text.getBytes(StandardCharsets.UTF_16).length;
    }
}
