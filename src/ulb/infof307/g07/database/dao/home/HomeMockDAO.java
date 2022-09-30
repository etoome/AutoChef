package ulb.infof307.g07.database.dao.home;

import ulb.infof307.g07.models.store.Home;

public class HomeMockDAO implements HomeDAO {
    Home currentHome;

    public HomeMockDAO() {
        currentHome = new Home(0, 0);
    }

    @Override
    public Boolean setCurrentHome(double lat, double lng) {
        currentHome = new Home((float) lat, (float) lng);
        return true;
    }

    @Override
    public Home getCurrentHome() {
        return currentHome;
    }
}
