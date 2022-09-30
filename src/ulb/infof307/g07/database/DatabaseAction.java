package ulb.infof307.g07.database;

import java.sql.SQLException;

@FunctionalInterface
public interface DatabaseAction<T, R> {
    R execute(T object) throws SQLException;
}
