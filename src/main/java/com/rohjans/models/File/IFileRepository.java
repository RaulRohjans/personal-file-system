package com.rohjans.models.File;

import com.rohjans.public_.tables.records.FileRecord;

import java.util.List;
import java.util.UUID;

/**
 * Interface that represents a File Repository class, which interacts with the database.
 *
 * @author Raul Rohjans 202100518
 */
public interface IFileRepository {
    /**
     * This method finds a file by ID.
     *
     * @param id ID of the file to be found.
     * @return Record of the file, or null if it wasn't found.
     */
    FileRecord findById(UUID id);

    /**
     * This method finds all the files in the database.
     *
     * @return List of the file records,
     */
    List<FileRecord> findAll();

    /**
     * This method creates a new file record.
     *
     * @param fileDTO File data object to be created in the database.
     * @return File record that was created or null if an error occurred.
     */
    FileRecord create(FileDTO fileDTO);

    /**
     * This method updates an existing file record in the database.
     *
     * @param fileDTO File data object to be updated.
     */
    void update(FileDTO fileDTO);

    /**
     * This method removes a file by its ID.
     *
     * @param id ID of the file to be removed from the database.
     */
    void removeById(UUID id);
}