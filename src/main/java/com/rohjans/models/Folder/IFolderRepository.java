package com.rohjans.models.Folder;

import com.rohjans.public_.tables.records.FolderRecord;

import java.util.List;
import java.util.UUID;

/**
 * Interface that represents a Folder Repository class, which interacts with the database.
 *
 * @author Raul Rohjans 202100518
 */
public interface IFolderRepository {
    /**
     * This method finds a folder by ID.
     *
     * @param id ID of the folder to be found.
     * @return Record of the folder, or null if it wasn't found.
     */
    FolderRecord findById(UUID id);

    /**
     * This method finds all the folders in the database.
     *
     * @return List of the folder records,
     */
    List<FolderRecord> findAll();

    /**
     * This method creates a new folder record.
     *
     * @param folderDTO Folder data object to be created in the database.
     * @return Folder record that was created or null if an error occurred.
     */
    FolderRecord create(FolderDTO folderDTO);

    /**
     * This method updates an existing folder record in the database.
     *
     * @param folderDTO Folder data object to be updated.
     */
    void update(FolderDTO folderDTO);

    /**
     * This method removes a folder by its ID.
     *
     * @param id ID of the folder to be removed from the database.
     */
    void removeById(UUID id);
}
