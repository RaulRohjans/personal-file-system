# Personal File System

<p align="center">
    <img src="https://github.com/user-attachments/assets/52db5f19-5319-4127-8c5c-23621b3d2bab" alt="PFS Main Menu"/>
    <img src="https://github.com/user-attachments/assets/4dc05bd4-4d24-42e3-9692-239262cb6875" alt="PFS Storage Graph"/>
</p>

## Overview
Personal File System (PFS) is a JavaFX-based application designed to efficiently manage files and folder structures. 
PFS leverages a data tree representation, making it user-friendly, and enabling easy navigation and interaction with files. Key functionalities include file operations like creation, movement, and editing, alongside unique features such as directory flattening and secured file access. This app also integrates a robust statistical component that provides insights into file handling metrics and storage utilization, presented through informative graphs.

## Features List

1. Visual Tree Representation: Utilizes a TreeView component for easy navigation across files and folders.
2. Metadata Panel:
    - Displays creation and modification dates, number of edits, file size, and importance level for selected files and folders.
    - Provides a text editing tab for .txt and .csv files.
3. File and Folder Operations:
    - Create, move, rename, and delete files and folders.
    - Flatten directories by moving all sub-folder files to the target folder and removing the empty directories.
    - File-specific operations like lock/unlock access with a password, and concatenate multiple files into one.
4. Search Functionality: Enables searching by file name or content through a unified search interface.
5. Data Management:
    - File deletion based on creation date or importance level.
    - Backup and restore functionality to manage and secure data effectively.
5. Statistical Insights:
    - Displays a pie chart of storage usage for selected directories within the PFS.
    - Offers metrics on file creation and edits, enhancing user awareness of storage practices.
6. Security Features:
    - Implements password-protected access to sensitive files using encryption.
7. User Interface:
    - Simple and intuitive layout with a comprehensive menu for all operations.
    - Information panel on the right provides detailed insights into the selected item's metadata.

## Setting up for development
### 1. Set java version to 17
The used java version for development is BellSoft Liberica 17.0.10, anything above this and JavaFX will not be able to be imported, since it is now a separate package and is no lunger bundled in the java devkit.

### 2. (Optional) Setup Postgres SQL instance
If you already have this, you skip this step, otherwise, you can setup an instance really quickly using the "docker-compose.yml" that is at the root of the project to do that for you.

After this, you can bash into the created container and create the database with the schema defined in the "schema.sql" file, which is also located in the project root.

### 3. Configure database connection in Maven
To configure this, open the "pom.xml" and scroll down to the plugins part, where the JOOQ plugin is configured, scroll further until you see the configuration, this has some placeholder items that should look like the following:

```
<!-- Specify your database details -->
<jdbc>
    <driver>org.postgresql.Driver</driver>
    <url>jdbc:postgresql://localhost:5432/pfs</url>
    <user>admin</user>
    <password>1234</password>
</jdbc>

....
```

Change these to your hearts content! This is what JOOQ will generate its classes based on.

### 4. Generate JOOQ's classes
Open a terminal inside the project's folder and run the following command:

```
mvn generate-sources
```

This will create all the needed classes inside a "target" folder on the root of the project folder

### 5. Mark JOOQ's classes as compiled directories
On IntelliJ the classes that were generated inside the "target" folder by maven will not be compiled by default, which will result in multiple errors across the application.
<br />
To change this, in IntelliJ, expand the "target" folder, this will reveal two sub-folders "classes" and "generated-sources", right click the "generated-sources" > "Mark Directory As" > "Generated Sources Root"
