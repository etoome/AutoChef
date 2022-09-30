package ulb.infof307.g07.controllers.map;

import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.WarningException;
import ulb.infof307.g07.models.store.Home;
import ulb.infof307.g07.models.store.exceptions.HomeException;
import ulb.infof307.g07.models.store.Store;
import ulb.infof307.g07.models.store.exceptions.StoreException;

public class MapJS {
    private final MapController mapController;

    MapJS(MapController m) {
        mapController = m;
    }

    public void print(String string) {
        System.out.println("[ JS ]: " + string);
    }

    /**
     * map.addMarketList(name); Method that adds the store on the side list and in the DB Store store = new Store(name,
     * lat, lng);
     *
     * @param storeName:
     *            name of the Store
     * @param lat
     *            : lattitude of the Store
     * @param lng
     *            : longitude of the Store
     */
    public void addStore(String storeName, double lat, double lng) {
        mapController.addStoreList(storeName);
        try {
            var store = new Store(storeName, lat, lng);
            Store.getDAO().createStore(store);
        } catch (StoreException e) {
            ExceptionManager.getInstance()
                    .handleException(new WarningException("Erreur lors du rajout du magasin.", e));
            return;
        }
        mapController.marketSelected(storeName);
    }

    public void closestStoreNotFound() {
        mapController.setError(MapController.errorNotMarketForShoppingList);
    }

    /**
     * Store store = new Store(name, lat, lng); Method that deletes the store on the side list and in the DB
     *
     * @param storeName:
     *            name of the Store
     * @param lat
     *            : lattitude of the Store
     * @param lng
     *            : longitude of the Store
     */
    public void deleteStore(String storeName, double lat, double lng) {
        try {
            var store = new Store(storeName, lat, lng);
            Store.getDAO().deleteStore(store);
        } catch (StoreException e) {
            ExceptionManager.getInstance()
                    .handleException(new WarningException("Erreur dans la suppression d'un magasin", e));
            return;
        }
        mapController.deleteMarketList(storeName);
    }

    public void setHome(double lat, double lng) {
        try {
            Home.getDAO().setCurrentHome(lat, lng);
        } catch (HomeException e) {
            ExceptionManager.getInstance().handleException(new WarningException("Erreur pour assigné votre Home", e));
        }
    }

    public void changeSelectedStore(String name) {
        mapController.marketSelected(name);
    }

    protected void finalize() throws Throwable {
        var a = 1;
        var b = a;
        // Doit etre redéfini pour permettre son fonctionnement dans la WebView
        // Vraiment jsp pourquoi, mais ça fait 2h je suis dessus, j'ai envie d'avancer là pas de débug JavaFX
    }
}
