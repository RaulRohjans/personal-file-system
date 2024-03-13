package com.rohjans.models.Folder;

import com.rohjans.models.File.FileDTO;
import com.rohjans.public_.tables.File;
import com.rohjans.public_.tables.records.FileRecord;
import com.rohjans.public_.tables.records.FolderRecord;
import com.rohjans.public_.tables.Folder;
import org.jooq.DSLContext;

import java.util.List;
import java.util.UUID;

/**
 * Class that represents a Folder Repository, which interacts with the database.
 *
 * @author Raul Rohjans 202100518
 */
public class FolderRepository implements IFolderRepository {
    /**
     * Database context instance.
     */
    private final DSLContext ctx;

    /**
     * Constructs the instance of the class.
     *
     * @param ctx Database context instance.
     */
    public FolderRepository(DSLContext ctx) {
        this.ctx = ctx;
    }

    /**
     * This method finds a folder by ID.
     *
     * @param id ID of the folder to be found.
     * @return Record of the folder, or null if it wasn't found.
     */
    @Override
    public FolderRecord findById(UUID id) {
        return ctx
            .selectFrom(Folder.FOLDER)
            .where(Folder.FOLDER.ID.eq(id))
            .fetchOne();
    }

    /**
     * This method finds all the folders in the database.
     *
     * @return List of the folder records,
     */
    @Override
    public List<FolderRecord> findAll() {
        return ctx
                .selectFrom(Folder.FOLDER)
                .fetch();
    }

    /**
     * This method creates a new folder record.
     *
     * @param folderDTO Folder data object to be created in the database.
     * @return Folder record that was created or null if an error occurred.
     */
    @Override
    public FolderRecord create(FolderDTO folderDTO) {
        FolderRecord folderRecord = ctx.newRecord(Folder.FOLDER);
        folderRecord.setId(folderDTO.getId());
        folderRecord.setParent(folderDTO.getParent());
        folderRecord.setName(folderDTO.getName());
        folderRecord.setCreated(folderDTO.getCreated());
        folderRecord.setChanged(folderDTO.getChanged());
        folderRecord.setChangecounter(folderDTO.getChangeCounter());

        if(folderRecord.store() != 1)
            throw new RuntimeException("Could not create 'Folder' record");

        return folderRecord;
    }

    /**
     * This method updates an existing folder record in the database.
     *
     * @param folderDTO Folder data object to be updated.
     */
    @Override
    public void update(FolderDTO folderDTO) {
        ctx.update(Folder.FOLDER)
                .set(Folder.FOLDER.PARENT, folderDTO.getParent())
                .set(Folder.FOLDER.NAME, folderDTO.getName())
                .set(Folder.FOLDER.CREATED, folderDTO.getCreated())
                .set(Folder.FOLDER.CHANGED, folderDTO.getChanged())
                .set(Folder.FOLDER.CHANGECOUNTER, folderDTO.getChangeCounter())

                .where(Folder.FOLDER.ID.eq(folderDTO.getId()))
                .execute();
    }

    /**
     * This method removes a folder by its ID.
     *
     * @param id ID of the folder to be removed from the database.
     */
    @Override
    public void removeById(UUID id) {
        //ctx.execute("delete cascade from ")
        ctx.delete(Folder.FOLDER)
            .where(Folder.FOLDER.ID.eq(id))
            .execute();
    }
}
