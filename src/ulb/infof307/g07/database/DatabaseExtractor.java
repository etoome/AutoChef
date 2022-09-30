package ulb.infof307.g07.database;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Map;

/**
 * Auxiliary object to the database that handles everything related to importing/exporting the database from and into
 * the jar, because your can't write to files directly inside the jar. This class is used only when the code is run from
 * .jar The general flow is: 1) At launch, extract the database from the jar into a temporary directory 2) While the
 * program is running, the database in the temporary directory is used 3) On shutdown, the external database is
 * re-imported into the jar, replacing the old database. This class has no written tests (only manual testing), because
 * it requires making a separate build that include the tests inside a new jar, to run the tests from inside that
 * jar.This requires too much time for the scope of this project.
 */
public class DatabaseExtractor {
    private final static String TEMP_DIR_PREFIX = "AutoChef_";
    private final static String RESOURCES_PATH_PREFIX = "/ulb/infof307/g07/";

    private Path tempDirPath = null;
    private Path extractedDatabasePath = null;
    private boolean isDatabaseExtracted = false;

    /**
     * Create a temporary directory starting with AutoChef_ The temporary directory is platform dependant
     */
    public void createTempDir() throws IOException {
        tempDirPath = Files.createTempDirectory(DatabaseExtractor.TEMP_DIR_PREFIX);
    }

    public boolean isTempDirCreated() {
        return tempDirPath != null;
    }

    public boolean isDatabaseExtracted() {
        return isDatabaseExtracted;
    }

    /**
     * Extract the database from the jar into the temporary directory
     */
    public void extractDatabaseToTempDir() {
        if (!isTempDirCreated()) {
            throw new RuntimeException("Temporary directory wasn't created before trying to extract database!");
        }
        if (extractedDatabasePath == null) {
            extractedDatabasePath = Paths.get(tempDirPath.toString(), Database.DATABASE_NAME);
        }
        var databasePathInsideJar = Path.of(RESOURCES_PATH_PREFIX, "database", Database.DATABASE_NAME);
        var databaseResource = getClass().getResource(databasePathInsideJar.toString());
        if (databaseResource == null) {
            throw new RuntimeException("Database was not found in the jar to be extracted!");
        }
        try (FileSystem zipfs = FileSystems.newFileSystem(databaseResource.toURI(), Map.of())) {
            var mountedDatabasePath = zipfs.getPath(databasePathInsideJar.toString());
            Files.copy(mountedDatabasePath, extractedDatabasePath);
        } catch (URISyntaxException exception) {
            throw new RuntimeException("Couldn't extract database path from resource url!\n" + exception.getMessage());
        } catch (IOException exception) {
            throw new RuntimeException("Failed to copy database to temp directory!\n" + exception.getMessage());
        }
        isDatabaseExtracted = true;
    }

    public String getExtractedDatabaseUrl() {
        return extractedDatabasePath.toString();
    }

    /**
     * Import database from temporary directory inside the jar, replacing the old one Implementation support for
     * mounting a zip filesystem at runtime can be found
     * <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html">here</a>
     */
    public void importDatabaseFromTempDirIntoJar() {
        var databasePathInsideJar = Path.of(RESOURCES_PATH_PREFIX, "database", Database.DATABASE_NAME);
        try (FileSystem zipfs = FileSystems.newFileSystem(
                // get the jar uri
                URI.create("jar:" + getClass().getProtectionDomain().getCodeSource().getLocation().toURI()),
                Map.of("create", "true"))) {
            Path mountedDatabasePath = zipfs.getPath(databasePathInsideJar.toString());
            Files.copy(extractedDatabasePath, mountedDatabasePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (URISyntaxException | IOException exception) {
            throw new RuntimeException("Failed to import back database inside jar!" + exception.getMessage());
        }
    }

    /**
     * Delete the temporary database and then the temporary directory
     */
    public void deleteTempContent() {
        try {
            Files.delete(extractedDatabasePath);
            Files.delete(tempDirPath);
        } catch (IOException ignored) {
            // not a problem if we fail to delete, it is inside a temp dir
            // it will be deleted on system restart
        }
    }
}
