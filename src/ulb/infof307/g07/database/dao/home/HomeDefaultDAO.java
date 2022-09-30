package ulb.infof307.g07.database.dao.home;

import ulb.infof307.g07.database.Database;
import ulb.infof307.g07.database.DatabaseException;
import ulb.infof307.g07.models.store.Home;
import ulb.infof307.g07.models.store.exceptions.HomeException;

import java.sql.ResultSet;
import java.util.List;

public class HomeDefaultDAO implements HomeDAO {
    @Override
    public Boolean setCurrentHome(double lat, double lng) throws HomeException {
        var db = Database.getInstance();

        try {
            return db.query("INSERT INTO Home(lat, lng) VALUES(?, ?)", List.of(lat, lng), ResultSet::next);
        } catch (DatabaseException e) {
            throw new HomeException("Error when setting current home ", e);
        }
    }

    @Override
    public Home getCurrentHome() throws HomeException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT lat, lng FROM Home", result -> {
                if (result.next())
                    return new Home(result.getFloat(1), result.getFloat(2));
                else
                    return null;
            });
        } catch (DatabaseException e) {
            throw new HomeException("Error when getting current home ", e);
        }
    }
}
