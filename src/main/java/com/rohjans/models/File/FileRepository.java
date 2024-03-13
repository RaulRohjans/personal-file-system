package com.rohjans.models.File;

import com.rohjans.public_.tables.records.FileRecord;
import com.rohjans.public_.tables.File;
import org.jooq.DSLContext;

import java.util.List;
import java.util.UUID;

/**
 * Class that represents a File Repository, which interacts with the database.
 *
 * @author Raul Rohjans 202100518
 */
public class FileRepository implements IFileRepository {
    /**
     * Database context instance.
     */
    private final DSLContext ctx;

    /**
     * Constructs the instance of the class.
     *
     * @param ctx Database context instance.
     */
    public FileRepository(DSLContext ctx) {
        this.ctx = ctx;
    }

    /**
     * This method finds a file by ID.
     *
     * @param id ID of the file to be found.
     * @return Record of the file, or null if it wasn't found.
     */
    @Override
    public FileRecord findById(UUID id) {
        return ctx
            .selectFrom(File.FILE)
            .where(File.FILE.ID.eq(id))
            .fetchOne();
    }

    /**
     * This method finds all the files in the database.
     *
     * @return List of the file records,
     */
    @Override
    public List<FileRecord> findAll() {
        return ctx
            .selectFrom(File.FILE)
            .fetch();
    }

    /**
     * This method creates a new file record.
     *
     * @param fileDTO File data object to be created in the database.
     * @return File record that was created or null if an error occurred.
     */
    @Override
    public FileRecord create(FileDTO fileDTO) {
        FileRecord fileRecord = ctx.newRecord(File.FILE);
        fileRecord.setId(fileDTO.getId());
        fileRecord.setParent(fileDTO.getParent());
        fileRecord.setName(fileDTO.getName());
        fileRecord.setCreated(fileDTO.getCreated());
        fileRecord.setChanged(fileDTO.getChanged());
        fileRecord.setChangecounter(fileDTO.getChangeCounter());
        fileRecord.setExtension(fileDTO.getExtension());
        fileRecord.setIslocked(fileDTO.isLocked());
        fileRecord.setFilesize(fileDTO.getFilesize());
        fileRecord.setImportance(fileDTO.getImportance());
        fileRecord.setPassword(fileDTO.getPassword());
        fileRecord.setContent(fileDTO.getContent());

        if(fileRecord.store() != 1)
            throw new RuntimeException("Could not create 'File' record");

        return fileRecord;
    }

    /**
     * This method updates an existing file record in the database.
     *
     * @param fileDTO File data object to be updated.
     */
    @Override
    public void update(FileDTO fileDTO) {
        ctx.update(File.FILE)
                .set(File.FILE.PARENT, fileDTO.getParent())
                .set(File.FILE.NAME, fileDTO.getName())
                .set(File.FILE.CREATED, fileDTO.getCreated())
                .set(File.FILE.CHANGED, fileDTO.getChanged())
                .set(File.FILE.CHANGECOUNTER, fileDTO.getChangeCounter())
                .set(File.FILE.EXTENSION, fileDTO.getExtension())
                .set(File.FILE.ISLOCKED, fileDTO.isLocked())
                .set(File.FILE.FILESIZE, fileDTO.getFilesize())
                .set(File.FILE.IMPORTANCE, fileDTO.getImportance())
                .set(File.FILE.PASSWORD, fileDTO.getPassword())
                .set(File.FILE.CONTENT, fileDTO.getContent())

                .where(File.FILE.ID.eq(fileDTO.getId()))
                .execute();
    }

    /**
     * This method removes a file by its ID.
     *
     * @param id ID of the file to be removed from the database.
     */
    @Override
    public void removeById(UUID id) {
        ctx.deleteFrom(File.FILE)
            .where(File.FILE.ID.eq(id))
            .execute();
    }
}
