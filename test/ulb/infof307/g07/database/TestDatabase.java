package ulb.infof307.g07.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestDatabase {

    @BeforeAll
    static void setDatabaseURL() throws NoSuchFieldException, IllegalAccessException {
        var urlField = Database.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(null, "jdbc:sqlite:test/ulb/infof307/g07/database/test.db");
    }

    void copyDatabaseFile() throws IOException {
        Path sourceDbPath = Paths.get("./resources/ulb/infof307/g07/database/" + Database.DATABASE_NAME)
                .toAbsolutePath();
        Path testDbPath = Paths.get("./test/ulb/infof307/g07/database/test.db").toAbsolutePath();
        Files.deleteIfExists(testDbPath);
        Files.copy(sourceDbPath, testDbPath);
    }

    void databaseInstanceIsNull() throws NoSuchFieldException, IllegalAccessException {
        // using reflection to get the "instance" field
        var instanceField = Database.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        var value = instanceField.get(null);
        // we are asserting it is null before running the test
        assert value == null;
    }

    void setDatabaseInstanceToNull() throws IllegalAccessException, NoSuchFieldException, DatabaseException {
        var db = Database.getInstance();
        if (db.isConnected()) {
            db.disconnect();
        }
        // using reflection to set the "instance" field to null
        var instanceField = Database.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        // call the GC so any finalize() gets called
        System.gc();
    }

    void deleteDatabaseFile() throws IOException {
        Path testDbPath = Paths.get("./test/ulb/infof307/g07/database/test.db").toAbsolutePath();
        Files.deleteIfExists(testDbPath);
    }

    @BeforeEach
    void beforeEach() throws IOException, NoSuchFieldException, IllegalAccessException {
        copyDatabaseFile();
        databaseInstanceIsNull();
    }

    @AfterEach
    void afterEach() throws NoSuchFieldException, DatabaseException, IllegalAccessException, IOException {
        setDatabaseInstanceToNull();
        deleteDatabaseFile();
    }

    @Test
    void getInstanceReturnsAnInstanceOfDatabase() {
        assertEquals(Database.class, Database.getInstance().getClass());
    }

    @Test
    void getInstanceReturnsSameInstanceOnSucessiveCalls() {
        final Database db_instance_1 = Database.getInstance();
        final Database db_instance_2 = Database.getInstance();
        final Database db_instance_3 = Database.getInstance();
        // we make sure they point to the same "address"
        assertEquals(System.identityHashCode(db_instance_1), System.identityHashCode(db_instance_2));
        assertEquals(System.identityHashCode(db_instance_2), System.identityHashCode(db_instance_3));
        assertEquals(System.identityHashCode(db_instance_3), System.identityHashCode(db_instance_1));
    }

    private Database getDatabase() {
        final var db = Database.getInstance();
        assertTrue(db.isConnected());
        return db;
    }

    private void disconnectDatabase(Database db) {
        try {
            db.disconnect();
        } catch (Exception exception) {
            // well we aren't testing this, but heck, make the test fail anyway
            throw new AssertionError(exception.getMessage());
        }
        assertFalse(db.isConnected());
    }

    private void assertThrowsDatabaseException(String expectedMessage, Executable executable) {
        var exception = assertThrows(DatabaseException.class, executable);
        var actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void gettingInstanceReturnsConnectedDatabase() {
        // Test might be unreliable because we can't? control the JDBC Driver
        getDatabase();
    }

    @Test
    void connectingToAlreadyConnectedDatabaseShouldFail() {
        final Database db = getDatabase();
        // May fail if the JDBC Driver decides to throw an error :S
        assertThrowsDatabaseException("Database is already connected!", db::connect);
    }

    @Test
    void disconnectingFromDisconnectedDatabaseShouldFail() {
        final Database db = getDatabase();
        disconnectDatabase(db);
        assertThrowsDatabaseException("Cannot close non-existent connection!", db::disconnect);
    }

    @Test
    void queryOnDisconnectedDatabaseShouldFail() {
        final Database db = getDatabase();
        disconnectDatabase(db);
        assertThrowsDatabaseException("We can't execute query without database connection", () -> db.query(null));
    }

    @Test
    void nullQueryShouldFail() {
        final Database db = getDatabase();
        assertThrowsDatabaseException("The query is null!", () -> db.query(null));
    }

    @Test
    void emptyQueryShouldFail() {
        final Database db = getDatabase();
        assertThrowsDatabaseException("The query is empty!", () -> db.query(""));
    }

    @Test
    void queryWithParamMismatchShouldFail() {
        final Database db = getDatabase();
        assertThrowsDatabaseException("Mismatch between the number of parameters and the count of ? in the query!",
                () -> db.query("SELECT * FROM Ingredient;", List.of(1, 2, 3)));
        assertThrowsDatabaseException("Mismatch between the number of parameters and the count of ? in the query!",
                () -> db.query(
                        "INSERT INTO IngredientRecipe(ingredient_id, recipe_id, quantity, unit) VALUES(?,?,?,?);",
                        List.of()));
        assertThrowsDatabaseException("Mismatch between the number of parameters and the count of ? in the query!",
                () -> db.query("INSERT INTO Recipe(name, number_people, type, instructions, style, time) "
                        + "VALUES(?,?,?,?,?,?);", List.of(1, 2, 3, 4, 5)));
    } // third test doesnt work anymore, it throws another kind of error but i dont know which one

    @Test
    void commitWithNoTransactionShouldFail() {
        final Database db = getDatabase();
        assertFalse(db.isInTransaction());
        assertThrowsDatabaseException("We can't commit transaction when none is currently active", db::commit);
    }

    @Test
    void rollbackWithNoTransactionShouldFail() {
        final Database db = getDatabase();
        assertFalse(db.isInTransaction());
        assertThrowsDatabaseException("We can't rollback transaction when none is currently active", db::rollback);
    }

    @Test
    void beginningMultipleTransactionsShouldFail() {
        final Database db = getDatabase();
        try {
            db.begin();
        } catch (DatabaseException exception) {
            // we aren't testing this but well, we should fail the test
            throw new AssertionError(exception.getMessage());
        }
        assertTrue(db.isInTransaction());
        assertThrowsDatabaseException("We can't begin a transaction when another one is currently active", db::begin);
    }

    private String getNameQuery(Database db) throws DatabaseException, SQLException {
        String name = db.query("SELECT name FROM Ingredient WHERE id=4;",
                result -> result.next() ? result.getString("name") : null);
        assertNotNull(name);
        return name;
    }

    @Test
    void commitAfterUpdateChangesValue() {
        final Database db = getDatabase();
        try {
            String oldName = getNameQuery(db);
            db.begin();
            db.query("UPDATE Ingredient SET name=? WHERE id=4;", List.of(oldName.toUpperCase()));
            db.commit();
            String updatedName = getNameQuery(db);
            assertEquals(oldName.toUpperCase(), updatedName);
            // set back to old name to reset db state
            db.query("UPDATE Ingredient SET name=? WHERE id=4;", List.of(oldName));
        } catch (DatabaseException | SQLException exception) {
            throw new AssertionError(exception.getMessage());
        }
    }

    @Test
    void rollbackAfterUpdateDoesntChangeValue() {
        final Database db = getDatabase();
        try {
            String oldName = getNameQuery(db);
            db.begin();
            db.query("UPDATE Ingredient SET name=? WHERE id=4;", List.of(oldName.toUpperCase()));
            db.rollback();
            String updatedName = getNameQuery(db);
            assertNotEquals(oldName.toUpperCase(), updatedName);
        } catch (DatabaseException | SQLException exception) {
            throw new AssertionError(exception.getMessage());
        }
    }
}