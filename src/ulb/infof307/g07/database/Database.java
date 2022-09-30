package ulb.infof307.g07.database;

import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.CriticalException;
import ulb.infof307.g07.utils.Config;

import java.io.IOException;
import java.sql.*;

/**
 * Database Singleton that serves to abstract the database. Exposes a generic API and Database transactions
 */
public final class Database {
    public static final String DRIVER_PREFIX = "jdbc:sqlite:";
    public static final String DATABASE_NAME = "AutoChefDB.db";
    private static Database instance = null;
    // Do not change path to DB with /resources/ instead of ::resource:,
    // it will not be found when packaged in the jar.
    // static because we need class access for reflection in testing
    private static String url = DRIVER_PREFIX + ":resource:ulb/infof307/g07/database/" + DATABASE_NAME;

    /**
     * Get the instance of the database singleton.
     *
     * @return the instance of the database
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private Connection connection;
    private DatabaseExtractor extractor;
    private boolean isInTransaction;

    private Database() {
        try {
            extractor = new DatabaseExtractor();
            if (Config.getInstance().isAppRunningFromJar()) {
                extractor.createTempDir();
                extractor.extractDatabaseToTempDir();
                Database.url = DRIVER_PREFIX + extractor.getExtractedDatabaseUrl();
            }
            this.connect();
        } catch (DatabaseException | IOException exception) {
            ExceptionManager.getInstance()
                    .handleException(new CriticalException("Erreur lors de la connection avec la DB", exception));
        }
        isInTransaction = false;
        Runtime.getRuntime().addShutdownHook(new Thread(this::databaseCleanUp));
    }

    /**
     * Clean up routine that gets executed when the application is closed normally
     */
    private void databaseCleanUp() {
        try {
            this.disconnect();
        } catch (DatabaseException ignored) {
            // we can't really do much if we fail to disconnect
            // this object is being destroyed anyway
        }
        if (extractor.isDatabaseExtracted()) {
            extractor.importDatabaseFromTempDirIntoJar();
        }
        if (extractor.isTempDirCreated()) {
            extractor.deleteTempContent();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        databaseCleanUp();
    }

    /**
     * Connect the database, implicitly called on construction.
     *
     * @throws DatabaseException
     *             when you try to reconnect to already connected database, or when you fail to connect to database.
     */
    public void connect() throws DatabaseException {
        try {
            if (this.isConnected()) {
                throw new DatabaseException("Database is already connected!");
            }
            this.connection = DriverManager.getConnection(Database.url);
        } catch (SQLException exception) {
            this.connection = null;
            throw new DatabaseException(String.format("Database failed to connect!\n%s", exception.getMessage()));
        }
    }

    /**
     * Disconnect the database, implicitly called when the object is deleted.
     *
     * @throws DatabaseException
     *             you cannot close a closed connection. Will also fail if driver fails.
     */
    public void disconnect() throws DatabaseException {
        try {
            if (!this.isConnected()) {
                throw new DatabaseException("Cannot close non-existent connection!");
            }
            this.connection.close();
            this.connection = null;
        } catch (SQLException exception) {
            throw new DatabaseException(String.format("Database failed to disconnect!\n%s", exception.getMessage()));
        }
    }

    /**
     * Is the database connected.
     *
     * @return True if the database is connected, else false
     */
    public boolean isConnected() {
        return this.connection != null;
    }

    /**
     * Are we currently in the middle of a transaction.
     *
     * @return True if the database is in the middle of a transaction.
     */
    public boolean isInTransaction() {
        return isInTransaction;
    }

    /**
     * Start a new database transaction.
     *
     * @throws DatabaseException
     *             when you try to start a new transaction, but we are already in one.
     */
    public void begin() throws DatabaseException {
        if (this.isInTransaction()) {
            throw new DatabaseException("We can't begin a transaction when another one is currently active");
        }
        this.query("BEGIN TRANSACTION;");
        this.isInTransaction = true;
    }

    /**
     * Commit a database transaction
     *
     * @throws DatabaseException
     *             when you try to commit with no active transaction.
     */
    public void commit() throws DatabaseException {
        if (!this.isInTransaction()) {
            throw new DatabaseException("We can't commit transaction when none is currently active");
        }
        this.query("COMMIT;");
        this.isInTransaction = false;
    }

    /**
     * Rollback a database transaction
     *
     * @throws DatabaseException
     *             when you try to rollback with no active transaction.
     */
    public void rollback() throws DatabaseException {
        if (!this.isInTransaction()) {
            throw new DatabaseException("We can't rollback transaction when none is currently active");
        }
        this.query("ROLLBACK;");
        this.isInTransaction = false;
    }

    /**
     * Main method for executing a database query.
     *
     * @param sqlQuery
     *            the sql (sqlite dialect) query in string format.
     * @param queryParams
     *            a list of objects that will be injected in the query where there are ?. Use List.of() for example.
     * @param statementOption
     *            Used when you want to recuperate the generated keys.
     * @param action
     *            to be executed once the query is done. A ResultSet is passed to the function/lambda. Will not be run
     *            for INSERT(unless generatedKeys)/UPDATE/DELETE.
     *
     * @return whatever is returned from the action.
     *
     * @throws DatabaseException
     *             you cannot execute a query with no connection, with a null query, or with an empty query.
     */
    public <T> T query(String sqlQuery, Iterable<Object> queryParams, Integer statementOption,
            DatabaseAction<ResultSet, T> action) throws DatabaseException {
        if (!this.isConnected()) {
            throw new DatabaseException("We can't execute query without database connection");
        }
        if (sqlQuery == null) {
            throw new DatabaseException("The query is null!");
        }
        if (sqlQuery.length() == 0) {
            throw new DatabaseException("The query is empty!");
        }
        PreparedStatement preparedStatement = prepareStatement(sqlQuery, queryParams, statementOption);
        try {
            return executeStatement(preparedStatement, action);
        } catch (SQLException exception) {
            throw new DatabaseException(String.format("Failed to close query resources!\n%s", exception.getMessage()));
        }
    }

    /**
     * Prepare statement to execute query. Verifies the integrity of the query and parameters.
     *
     * @param sqlQuery
     *            the sql (sqlite dialect) query string.
     * @param queryParams
     *            the list of query object parameters that will be injected into the '?' of the query
     * @param statementOption
     *            the options for generated keys.
     *
     * @return {@link PreparedStatement} ready to be executed.
     *
     * @throws DatabaseException
     *             when there is a mismatch between the number of '?' in the query and the number of parameters passed.
     */
    private PreparedStatement prepareStatement(String sqlQuery, Iterable<Object> queryParams, Integer statementOption)
            throws DatabaseException {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = statementOption == null ? this.connection.prepareStatement(sqlQuery)
                    : this.connection.prepareStatement(sqlQuery, statementOption);
            final var questionMarksCount = sqlQuery.chars().filter(x -> x == '?').count();
            int idx = 0;
            if (queryParams != null) {
                for (var param : queryParams) {
                    if (++idx <= questionMarksCount) {
                        preparedStatement.setObject(idx, param);
                    }
                }
            }
            if (idx != questionMarksCount) {
                throw new DatabaseException(
                        "Mismatch between the number of parameters and the count of ? in the query!");
            }
        } catch (SQLException exception) {
            throw new DatabaseException(String.format("Query failed during preparation!\n%s", exception.getMessage()));
        }
        return preparedStatement;
    }

    /**
     * Execute a prepared statement
     *
     * @param preparedStatement
     *            a prepared statement that was created with
     *            {@link Database#prepareStatement(String, Iterable, Integer)}
     * @param action
     *            to be executed once the query is done. A ResultSet is passed to the function/lambda. Will not be run
     *            for INSERT(unless generatedKeys)/UPDATE/DELETE.
     *
     * @return whatever the action returns.
     *
     * @throws DatabaseException
     *             when the driver fails to execute the query.
     *
     * @see Database#query(String, Iterable, Integer, DatabaseAction)
     */
    private <T> T executeStatement(PreparedStatement preparedStatement, DatabaseAction<ResultSet, T> action)
            throws DatabaseException, SQLException {
        ResultSet queryResults = null;
        try {
            var hasResults = preparedStatement.execute();
            if (hasResults) {
                queryResults = preparedStatement.getResultSet();
            } else {
                var generatedKeys = preparedStatement.getGeneratedKeys();
                boolean isResultSetNotEmpty = generatedKeys.isBeforeFirst(); // JDBC's API sucks (RTFM)
                if (isResultSetNotEmpty) {
                    queryResults = generatedKeys;
                }
            }
            if (action != null && queryResults != null) {
                return action.execute(queryResults);
            } else {
                return null;
            }
        } catch (SQLException exception) {
            throw new DatabaseException(String.format("Query failed during execution!\n%s", exception.getMessage()));
        } finally {
            if (queryResults != null && !queryResults.isClosed()) {
                queryResults.close();
            }
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Overload of {@link Database#query(String, Iterable, Integer, DatabaseAction)}.
     *
     * @see Database#query(String, Iterable, Integer, DatabaseAction)
     */
    public <T> T query(String sqlQuery, Iterable<Object> queryParams) throws DatabaseException {
        return query(sqlQuery, queryParams, null, null);
    }

    /**
     * Overload of {@link Database#query(String, Iterable, Integer, DatabaseAction)}.
     *
     * @see Database#query(String, Iterable, Integer, DatabaseAction)
     */
    public <T> T query(String sqlQuery) throws DatabaseException {
        return query(sqlQuery, null, null, null);
    }

    /**
     * Overload of {@link Database#query(String, Iterable, Integer, DatabaseAction)}.
     *
     * @see Database#query(String, Iterable, Integer, DatabaseAction)
     */
    public <T> T query(String sqlQuery, Iterable<Object> queryParams, DatabaseAction<ResultSet, T> action)
            throws DatabaseException {
        return query(sqlQuery, queryParams, null, action);
    }

    /**
     * Overload of {@link Database#query(String, Iterable, Integer, DatabaseAction)}.
     *
     * @see Database#query(String, Iterable, Integer, DatabaseAction)
     */
    public <T> T query(String sqlQuery, DatabaseAction<ResultSet, T> action) throws DatabaseException {
        return query(sqlQuery, null, null, action);
    }
}