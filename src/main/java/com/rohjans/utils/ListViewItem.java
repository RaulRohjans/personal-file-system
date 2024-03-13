package com.rohjans.utils;

import javafx.scene.control.TreeItem;
import com.rohjans.models.ItemDTO;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a class that defines a list view item for the item search view.
 *
 * @author Raul Rohjans 202100518
 */
public class ListViewItem {
    /**
     * The value item of the ListViewItem.
     */
    TreeItem<ItemDTO> item;

    /**
     * Constructs a new instance of the class.
     *
     * @param item Item to be hold on the list view item.
     */
    public ListViewItem(TreeItem<ItemDTO> item) { this.item = item; }

    /**
     * This method overrides the default toString operation.
     * The reason we need is to have the correct value be displayed on the list view.
     *
     * @return The path of the file in the PFS.
     */
    @Override
    public String toString() {
        List<String> path = new ArrayList<>();
        getPathToRootHelper(this.getItem(), path);

        //Reverse to get the path in the right order
        Collections.reverse(path);

        return "/" + String.join("/", path);
    }

    /**
     * This method is a helper to the toString function, its purpose
     * is to calculate the path of the item.
     *
     * @param item Item to calculate the path to.
     * @param path List of item names that will later be used to build the path with a join.
     */
    private void getPathToRootHelper(TreeItem<ItemDTO> item, List<String> path) {
        if (item.getParent() == null) return; //Root node

        path.add(item.getValue().getName());
        getPathToRootHelper(item.getParent(), path); // Recurse up the tree
    }

    /**
     * This method is a getter to the item.
     *
     * @return The tree item.
     */
    public TreeItem<ItemDTO> getItem() {
        return item;
    }

    /**
     * This method is a setter for the item.
     *
     * @param item Item that will be set in place of the current item.
     */
    public void setItem(TreeItem<ItemDTO> item) {
        this.item = item;
    }
}
