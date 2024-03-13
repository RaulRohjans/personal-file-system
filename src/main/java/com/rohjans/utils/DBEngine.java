package com.rohjans.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import com.rohjans.controllers.DatabaseConfigController;
import com.rohjans.models.DbBackupData;
import com.rohjans.models.DbConfig;
import com.rohjans.models.File.FileMapper;
import com.rohjans.models.File.FileRepository;
import com.rohjans.models.Folder.FolderMapper;
import com.rohjans.models.Folder.FolderRepository;
import com.rohjans.public_.tables.Folder;

import java.io.*;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

/**
 * This is the class that holds all the database related methods, used to manipulate persistent data.
 *
 * @author Raul Rohjans 202100518
 */
public class DBEngine {
    /**
     * Database configuration object which holds connection data.
     */
    private static DbConfig dbConfig = null;

    /**
     * This method gets a database context instance.
     *
     * @return The instance of the database connection context.
     * @throws SQLException Happens if the connection to the database cannot be established.
     */
    public static DSLContext getDslContext() throws SQLException {
        loadUserDbConfig(); //Load db config from user

        //If the configuration came out empty we exit the program
        //This will happen if the user closes the DatabaseConfig form window
        //And at least one of the fields is empty
        if(!validateDbConfig(dbConfig)) System.exit(0);

        String username = dbConfig.username();
        String password = dbConfig.password();
        String url = "jdbc:postgresql://" + dbConfig.hostname() + ":" + dbConfig.port()
                + "/pfs?stringtype=unspecified";

        /*
        * These settings are needed to fix an issue with record insertion where
        * it would try to return the inserted record and fetch its key through the _rowid_ field
        * but since this field does not exist, and we handle the record id creation on the client, it
        * has to be turned off to avoid problems
        * */
        Settings settings = new Settings();
        settings.setReturnIdentityOnUpdatableRecord(false);

        Connection conn = DriverManager.getConnection(url, username, password);
        return DSL.using(conn, SQLDialect.SQLITE, settings);
    }

    /**
     * This method asks the user for the database connection credentials.
     */
    private static void loadUserDbConfig() {
        //If data was loaded before, no need to ask user for it again
        if(dbConfig != null) return;

        try {
            DatabaseConfigController dbConfigController = DatabaseConfigController.create();

            //Save result
            dbConfig = dbConfigController.show();
        }
        catch (Exception e) {
            Helpers.showErrorDialog(e);
        }
    }

    /**
     * This method validates a database configuration object.
     *
     * @param cfg Database configuration object.
     * @return True if the config is valid, or else false.
     */
    private static boolean validateDbConfig(DbConfig cfg) {
        if(cfg.username().isEmpty()) return false;
        if(cfg.password().isEmpty()) return false;
        if(cfg.hostname().isEmpty()) return false;
        if(cfg.port().isEmpty()) return false;

        return true;
    }

    /**
     * This method generates a new primary key for database records.
     *
     * @return Generated UUID.
     */
    public static UUID getNewPrimaryKey() {
        return UUID.randomUUID();
    }

    /**
     * This method is used to back up the current database instance data.
     * <p>
     * NEEDS REFACTOR
     *    Jooq doesn't have any utility to back up and restore databases, so we are doing
     *    this manually by fetching and storing all the files and folders in a file.
     *    This is going to give a lot of problems if the schema is changed and an "old" backup is imported.
     *
     * @param directory Directory location of the backup file.
     * @throws SQLException May happen if connection to the database cannot be established.
     * @throws IOException Will happen if the file cannot be written to the file system.
     */
    public static void backupDbData(String directory) throws SQLException, IOException {
        if(directory == null || directory.isEmpty()) return;

        DSLContext ctx = getDslContext();
        FileRepository fileRepository = new FileRepository(ctx);
        FolderRepository folderRepository = new FolderRepository(ctx);

        /* Instance backup object
        * The reason we save the backup as a DTO instead of using Jooq's generated class (FileRecord and FolderRecord)
        * is because those are not serializable.
        *
        * This method creates a lot of overhead and is quite slow if a lot of items are involved, but should be fine
        * for a small application like this.
        * */
        DbBackupData backupData = new DbBackupData(FileMapper.toFileDTO(fileRepository.findAll()),
                FolderMapper.toFolderDTO(folderRepository.findAll()));

        //Serialize data to JSON
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.findAndRegisterModules(); //This is used for TypeHandling

        ObjectWriter ow = objMapper.writer().withDefaultPrettyPrinter();
        String jsonData = ow.writeValueAsString(backupData);

        //Create backup file
        File backupFile = Paths.get(directory, System.currentTimeMillis() + "_PFS_BK.json").toFile();

        if(!backupFile.createNewFile()) {
            Helpers.showErrorDialog("Could not create backup file, it already exists on the choosen path!");
            return;
        }

        //Write json string to file
        FileWriter writer = new FileWriter(backupFile);
        writer.write(jsonData);
        writer.close();
    }

    /**
     * This method is used to restore the database state from a backup file.
     * <p>
     * NEEDS REFACTOR
     *    Jooq doesn't have any utility to back up and restore databases, so we are doing
     *    this manually by fetching and storing all the files and folders in a file.
     *    This is going to give a lot of problems if the schema is changed and an "old" backup is imported.
     *
     * @param file File to get the database data from.
     * @throws SQLException May happen if connection to the database cannot be established.
     * @throws IOException Will happen if the file cannot be read from the filesystem.
     */
    public static void restoreDbData(File file) throws SQLException, IOException {
        if(file == null || !file.exists()) return;

        //Deserialize json file
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); //This is used for TypeHandling

        DbBackupData dbBackupData = mapper.readValue(file, DbBackupData.class);

        /* Update DB */
        DSLContext ctx = getDslContext();

        //Remove all data, starting with files
        ctx.deleteFrom(com.rohjans.public_.tables.File.FILE).execute();
        ctx.deleteFrom(Folder.FOLDER).execute();

        //Add imported data to DB
        ctx.batchInsert(FolderMapper.toFolderRecord(dbBackupData.getFolders())).execute(); //Folders have to be first due to FKs
        ctx.batchInsert(FileMapper.toFileRecord(dbBackupData.getFiles())).execute();;
        /* -------- */
    }

    /*
    * Getters
    * */

    /**
     * Getter for the database configuration object.
     *
     * @return The database configuration object instance.
     */
    public static DbConfig getDbConfig() {
        return dbConfig;
    }
}
