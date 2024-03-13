package com.rohjans.models.Folder;

import com.rohjans.public_.tables.records.FolderRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a static class that is only used to map FolderDTOs to FolderRecords and vice versa.
 *
 * @author Raul Rohjans 202100518
 */
public class FolderMapper {
    /**
     * This method converts a FolderRecord into a FolderDTO.
     *
     * @param folderRecord Folder record to be converted.
     * @return Converted folder data object.
     */
    public static FolderDTO toFolderDTO(FolderRecord folderRecord) {
        if(folderRecord == null) return null;

        return new FolderDTO(
                folderRecord.getId(),
                folderRecord.getParent(),
                folderRecord.getName(),
                folderRecord.getCreated(),
                folderRecord.getChanged(),
                folderRecord.getChangecounter()
        );
    }

    /**
     * This method converts a list of FolderRecord into a list of FolderDTO.
     *
     * @param folderRecords Folder records to be converted.
     * @return Converted folder data objects.
     */
    public static List<FolderDTO> toFolderDTO(List<FolderRecord> folderRecords) {
        ArrayList<FolderDTO> files = new ArrayList<>();

        if(folderRecords == null) return files;

        for(FolderRecord record : folderRecords)
            files.add(toFolderDTO(record));

        return files;
    }

    /**
     * This method converts a folder data object into a FolderRecord.
     *
     * @param folderDTO Folder data object to be converted.
     * @return Converted folder record.
     */
    public static FolderRecord toFolderRecord(FolderDTO folderDTO) {
        if(folderDTO == null) return null;

        return new FolderRecord(
                folderDTO.getId(),
                folderDTO.getParent(),
                folderDTO.getName(),
                folderDTO.getCreated(),
                folderDTO.getChanged(),
                folderDTO.getChangeCounter()
        );
    }

    /**
     * This method converts a folder data object list into a list of FolderRecord.
     *
     * @param folderDTOS Folder data objects to be converted.
     * @return List of the converted folder records.
     */
    public static List<FolderRecord> toFolderRecord(List<FolderDTO> folderDTOS) {
        ArrayList<FolderRecord> files = new ArrayList<>();

        if(folderDTOS == null) return files;

        for(FolderDTO record : folderDTOS)
            files.add(toFolderRecord(record));

        return files;
    }
}
