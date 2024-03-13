package com.rohjans.models.Folder;

import com.rohjans.models.ItemDTO;
import com.rohjans.utils.DBEngine;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This is a class that represents Folder data object.
 *
 * @author Raul Rohjans 202100518
 */
public class FolderDTO extends ItemDTO {
    /**
     * Default constructor used for serializing and deserializing the object.
     */
    public FolderDTO() {}

    /**
     * Constructs the folder data object with all the fields.
     *
     * @param id ID of the item.
     * @param parent ID of the item's parent.
     * @param name Name of the item.
     * @param created Date and time of when the item was created.
     * @param changed Date and time of when the item was changed.
     * @param changeCounter The amount of times the item has been changed.
     */
    public FolderDTO(UUID id, UUID parent, String name, LocalDateTime created, LocalDateTime changed, int changeCounter) {
        super(id, parent, name, created, changed, changeCounter);
    }

    /**
     * Constructs the folder data object with only the essential.
     *
     * @param parent ID of the parent of the folder.
     * @param name Name of the folder.
     */
    public FolderDTO(UUID parent, String name) {
        super(DBEngine.getNewPrimaryKey(), parent, name, LocalDateTime.now(), null, 0);
    }
}