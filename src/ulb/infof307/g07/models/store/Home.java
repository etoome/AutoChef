package ulb.infof307.g07.models.store;

import ulb.infof307.g07.database.dao.DAO;
import ulb.infof307.g07.database.dao.home.HomeDAO;

/** Represents the Home on the map */
public class Home {
    private final Position position;

    public Home(double lat, double lng) {
        this.position = new Position(lat, lng);
    }

    /** @return get the longtitude of the home */
    public double getLng() {
        return position.lng();
    }

    /** @return get the latitude of the home */
    public double getLat() {
        return position.lat();
    }

    public static HomeDAO getDAO() {
        return DAO.getInstance().getHomeDAO();
    }
}
