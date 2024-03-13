package com.rohjans.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import com.rohjans.utils.DBEngine;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This is a class that represents a PFS item.
 *
 * @author Raul Rohjans 202100518
 */
public class ItemDTO implements Serializable {
    /**
     * ID of the item.
     */
    private UUID id;

    /**
     * ID of this item's parent.
     */
    private UUID parent;

    /**
     * Name of the item. We use a StringProperty, so we can have a listener when the value changes.
     */
    private final StringProperty name;

    /**
     * Date and time of when the item was created.
     */
    private LocalDateTime created;

    /**
     * Date and time of when the item was last changed.
     */
    private LocalDateTime changed;

    /**
     * The amount of times the item has been changed.
     */
    private int changeCounter;


    /*
    * Constructors
    * */

    /**
     * This is the default constructor, which just generates a new ID and instances the StringProperty for the name.
     */
    public ItemDTO() {
        this.id = DBEngine.getNewPrimaryKey();
        this.name = new SimpleStringProperty();
    }

    /**
     * This constructor is used to fill the value on all the class fields.
     *
     * @param id ID of the item.
     * @param parent ID of the item's parent.
     * @param name Name of the item.
     * @param created Date and time of when the item was created.
     * @param changed Date and time of when the item was changed.
     * @param changeCounter The amount of times the item has been changed.
     */
    public ItemDTO(UUID id, UUID parent, String name, LocalDateTime created, LocalDateTime changed, int changeCounter) {
        this.id = id;
        this.parent = parent;
        this.name = new SimpleStringProperty(name);
        this.created = created;
        this.changed = changed;
        this.changeCounter = changeCounter;
    }


    /*
    * Class methods
    * */

    /**
     * Override of the toString method, this will be used to display the name of the item int he PFS tree.
     *
     * @return Name of the item.
     */
    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * This method clones the current item instance.
     *
     * @return Returns a new equal instance to the current one.
     */
    public ItemDTO clone() {
        return new ItemDTO(
                DBEngine.getNewPrimaryKey(),
                this.parent,
                this.getName(),
                this.created,
                this.changed,
                this.changeCounter
        );
    }

    /**
     * This method increments the change counter.
     */
    public void incrementCounter() { this.changeCounter++; }

    /**
     * This method updates the changed date with the current date and time.
     */
    public void updateChanged() { this.changed = LocalDateTime.now(); }


    /*
    * Getters and setters
    * */

    /**
     * Getter for the item's ID.
     *
     * @return Item ID.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Setter for the item ID.
     *
     * @param id ID of the item.
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Getter for the item's parent ID.
     *
     * @return ID of the parent.
     */
    public UUID getParent() {
        return parent;
    }

    /**
     * Setter for the item's parent ID.
     *
     * @param parent ID of the parent.
     */
    public void setParent(UUID parent) {
        this.parent = parent;
    }

    /**
     * Getter for the name of the item.
     *
     * @return Name of the item.
     */
    public String getName() {
        return name.getValue();
    }

    /**
     * Setter for the name of the item.
     * This will change the changed date and increment the change counter.
     *
     * @param name Name of the item.
     */
    public void setName(String name) {
        this.name.setValue(name);
        this.updateChanged();
        this.incrementCounter();
    }

    /**
     * Getter for the entire name property object.
     *
     * @return The StringProperty corresponding to the name of the item.
     */
    public StringProperty nameProperty() {
        return this.name;
    }

    /**
     * Getter for the creation date.
     *
     * @return Creation date.
     */
    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * Setter for the creation date.
     *
     * @param created Item creation date.
     */
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    /**
     * Getter for the changed date.
     *
     * @return Item's changed date.
     */
    public LocalDateTime getChanged() {
        return changed;
    }

    /**
     * Setter for the changed date.
     *
     * @param changed Items changed date.
     */
    public void setChanged(LocalDateTime changed) {
        this.changed = changed;
    }

    /**
     * Getter for the change counter.
     *
     * @return Item's change counter.
     */
    public int getChangeCounter() {
        return changeCounter;
    }

    /**
     * Setter for the change counter.
     *
     * @param changeCounter Item's change counter.
     */
    public void setChangeCounter(int changeCounter) {
        this.changeCounter = changeCounter;
    }

}
