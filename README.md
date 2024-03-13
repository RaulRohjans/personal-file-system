[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/YivFRH7G)

# Projeto ER

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