package com.rohjans.models.File;

import com.rohjans.public_.tables.records.FileRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a static class that is only used to map FileDTOs to FileRecords and vice versa.
 *
 * @author Raul Rohjans 202100518
 */
public class FileMapper {
    /**
     * This method converts a FileRecord into a FileDTO.
     *
     * @param fileRecord File record to be converted.
     * @return Converted file data object.
     */
    public static FileDTO toFileDTO(FileRecord fileRecord) {
        if(fileRecord == null) return null;

        return new FileDTO(
                fileRecord.getId(),
                fileRecord.getParent(),
                fileRecord.getName(),
                fileRecord.getCreated(),
                fileRecord.getChanged(),
                fileRecord.getChangecounter(),
                fileRecord.getExtension(),
                fileRecord.getIslocked(),
                fileRecord.getFilesize(),
                fileRecord.getImportance(),
                fileRecord.getPassword(),
                fileRecord.getContent()
        );
    }

    /**
     * This method converts a list of FileRecord into a list of FileDTO.
     *
     * @param fileRecords File records to be converted.
     * @return Converted file data objects.
     */
    public static List<FileDTO> toFileDTO(List<FileRecord> fileRecords) {
        if(fileRecords == null) return null;

        ArrayList<FileDTO> files = new ArrayList<>();
        for(FileRecord record : fileRecords)
            files.add(toFileDTO(record));

        return files;
    }

    /**
     * This method converts a file data object into a FileRecord.
     *
     * @param fileDTO File data object to be converted.
     * @return Converted file record.
     */
    public static FileRecord toFileRecord(FileDTO fileDTO) {
        if(fileDTO == null) return null;

        return new FileRecord(
                fileDTO.getId(),
                fileDTO.getParent(),
                fileDTO.getName(),
                fileDTO.getCreated(),
                fileDTO.getChanged(),
                fileDTO.getChangeCounter(),
                fileDTO.getExtension(),
                fileDTO.isLocked(),
                fileDTO.getFilesize(),
                fileDTO.getImportance(),
                fileDTO.getPassword(),
                fileDTO.getContent()
        );
    }

    /**
     * This method converts a file data object list into a list of FileRecord.
     *
     * @param fileDTOS File data objects to be converted.
     * @return List of the converted file records.
     */
    public static List<FileRecord> toFileRecord(List<FileDTO> fileDTOS) {
        ArrayList<FileRecord> files = new ArrayList<>();

        if(fileDTOS == null) return files;

        for(FileDTO record : fileDTOS)
            files.add(toFileRecord(record));

        return files;
    }
}
