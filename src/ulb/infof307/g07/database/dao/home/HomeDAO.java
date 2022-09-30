package ulb.infof307.g07.database.dao.home;

import ulb.infof307.g07.models.store.Home;
import ulb.infof307.g07.models.store.exceptions.HomeException;

public interface HomeDAO {
    Boolean setCurrentHome(double lat, double lng) throws HomeException;

    Home getCurrentHome() throws HomeException;
}
