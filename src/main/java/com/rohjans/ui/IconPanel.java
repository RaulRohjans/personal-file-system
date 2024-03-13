package com.rohjans.ui;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import com.rohjans.utils.Helpers;

import java.io.FileNotFoundException;

/**
 * This class was created with the sole purpose of adding icons to the tree items in the PFS.
 *
 * @author Raul Rohjans 202100518
 */
public class IconPanel extends Pane {
    /**
     * Constructor that instances the UI portion of the class and sets the provided image as the icon.
     *
     * @param image Icon of the item to be set.
     */
    public IconPanel(final String image) {
        try {
            HBox pane = new HBox(5.0);

            ImageView icon = new ImageView(Helpers.getResourceIcon(image));
            icon.setFitHeight(16);
            icon.setFitWidth(16);

            pane.getChildren().add(icon);

            this.getChildren().add(pane);
        } catch (FileNotFoundException e) {
            Helpers.showErrorDialog(e);
        }
    }
}