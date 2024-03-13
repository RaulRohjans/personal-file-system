package com.rohjans.models;

import com.rohjans.models.File.FileDTO;
import com.rohjans.models.Folder.FolderDTO;
import com.rohjans.public_.tables.records.FileRecord;
import com.rohjans.public_.tables.records.FolderRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that represents a Database backup data object.
 * <p>
 * It is used to save to import and export the PFS content.
 *
 * @author Raul Rohjans 202100518
 */
public class DbBackupData {
    /**
     * Files in the PFS.
     */
    private List<FileDTO> files;

    /**
     * Folders in the PFS.
     */
    private List<FolderDTO> folders;

    /**
     * This is the default constructor which is only used to serialize and deserialize this object.
     */
    public DbBackupData() {
        files = new ArrayList<>();
        folders = new ArrayList<>();
    }

    /**
     * Constructor with all the fields.
     *
     * @param files List of files to save in the backup object.
     * @param folders List of folders to save in the backup object.
     */
    public DbBackupData(List<FileDTO> files, List<FolderDTO> folders) {
        this.files = files;
        this.folders = folders;
    }

    /**
     * Getter for the files.
     *
     * @return Files in the backup object.
     */
    public List<FileDTO> getFiles() {
        return files;
    }

    /**
     * Setter for the files.
     *
     * @param files Files to save in the backup.
     */
    public void setFiles(List<FileDTO> files) {
        this.files = files;
    }

    /**
     * Getter for the folders.
     *
     * @return Folders in the backup object.
     */
    public List<FolderDTO> getFolders() {
        return folders;
    }

    /**
     * Setter for the folders.
     *
     * @param folders Folders to set in the backup object.
     */
    public void setFolders(List<FolderDTO> folders) {
        this.folders = folders;
    }
}
