package com.rohjans.models.File;

import com.rohjans.models.ItemDTO;
import com.rohjans.utils.DBEngine;
import com.rohjans.utils.Helpers;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This is a class that represents File data object.
 *
 * @author Raul Rohjans 202100518
 */
public class FileDTO extends ItemDTO {
    /**
     * Extension of the file.
     */
    private String extension;

    /**
     * If the file is password protected, AKA if its locked.
     */
    private boolean isLocked;

    /**
     * The size of the file content.
     */
    private BigDecimal filesize;

    /**
     * The importance of the file on a scale of 0 to 4.
     */
    private int importance;

    /**
     * The password hash of the file when its locked.
     */
    private String password;

    /**
     * Text content of the file (only used in txt and csv files)
     */
    private String content;


    /*
    * Constructors
    * */

    /**
     * Default constructor used for serializing and deserializing the object.
     */
    public FileDTO() { }

    /**
     * Constructs the file data object with all the fields.
     *
     * @param id ID of the item.
     * @param parent ID of the item's parent.
     * @param name Name of the item.
     * @param created Date and time of when the item was created.
     * @param changed Date and time of when the item was changed.
     * @param changeCounter The amount of times the item has been changed.
     * @param extension Extension of the file.
     * @param isLocked If the file is password protected AKA locked.
     * @param filesize Size of the file content.
     * @param importance Importance of the file on a scale of 0 to 4.
     * @param password Password of the file if locked.
     * @param content Content of the file (can only be used with txt or csv files).
     */
    public FileDTO(UUID id, UUID parent, String name, LocalDateTime created, LocalDateTime changed,
                   int changeCounter, String extension, boolean isLocked, BigDecimal filesize,
                   int importance, String password, String content) {
        super(id, parent, name, created, changed, changeCounter);
        this.extension = extension;
        this.isLocked = isLocked;
        this.filesize = filesize;
        this.importance = importance;
        this.password = password;
        this.content = content;
    }

    /**
     * Constructs the file data object with only the essential.
     *
     * @param parent ID of the parent of the file.
     * @param name Name of the file.
     * @param extension Extension of the file.
     */
    public FileDTO(UUID parent, String name, String extension) {
        super(DBEngine.getNewPrimaryKey(), parent, name, LocalDateTime.now(), null, 0);
        this.extension = extension;
        this.isLocked = false;
        this.filesize = BigDecimal.valueOf(0);
        this.importance = 0;
    }


    /*
    * Methods
    * */

    /**
     * This method sets the name of the file on the StringProperty.
     *
     * @param name Name to be set on the file.
     */
    @Override
    public void setName(String name) {
        super.setName(name);

        //Update file extension
        this.extension = Helpers.parseFileExtension(name);
    }

    /**
     * This method clones the current file object instance.
     *
     * @return A duplicate of the current file object instance.
     */
    @Override
    public ItemDTO clone() {
        return new FileDTO(
            DBEngine.getNewPrimaryKey(),
            this.getParent(),
            this.getName(),
            this.getCreated(),
            this.getChanged(),
            this.getChangeCounter(),
            this.getExtension(),
            this.isLocked(),
            this.getFilesize(),
            this.getImportance(),
            this.getPassword(),
            this.getContent()
        );
    }

    /**
     * This method updates the file size by checking its content.
     */
    public void updateFileSize() {
        this.filesize = BigDecimal.valueOf(Helpers.calculateStringSizeInBytes(this.content));
    }


    /*
    * Getters and Setters
    * */

    /**
     * Getter for the file extension.
     *
     * @return The extension of the file.
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Setter for the file extension.
     *
     * @param extension Extension to be set.
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * Getter for the is locked property.
     *
     * @return True if the file is locked, otherwise false.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Setter for the file locked state.
     *
     * @param locked State of the lock to be set on the file.
     */
    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    /**
     * Getter for the file's size.
     *
     * @return Size of the file.
     */
    public BigDecimal getFilesize() {
        return this.filesize;
    }

    /**
     * Another getter for the size of the file, but in the format of a long instead of BigDecimal.
     *
     * @return Size of the file in long type.
     */
    public long getLongFileSize() {
        return filesize.longValue();
    }

    /**
     * Setter for the size of the file.
     *
     * @param filesize Size of the file to be set.
     */
    public void setFilesize(BigDecimal filesize) {
        this.filesize = filesize;
    }

    /**
     * Getter for the importance of the file.
     *
     * @return Importance of the file.
     */
    public int getImportance() {
        return importance;
    }

    /**
     * Setter for the importance of the file.
     *
     * @param importance Importance to be set on the file (from 0 to 4).
     */
    public void setImportance(int importance) {
        this.importance = importance;
    }

    /**
     * Getter for the file's password hash.
     *
     * @return Hash of the file password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for the file password hash.
     *
     * @param password Password hash to be set on the file.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for the content of the file.
     *
     * @return Text content of the file, or null if invalid extension.
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter for the file content.
     *
     * @param content Content to be set on the file.
     */
    public void setContent(String content) {
        this.content = content;
    }
}