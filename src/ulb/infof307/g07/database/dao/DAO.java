package ulb.infof307.g07.database.dao;

import ulb.infof307.g07.database.dao.agenda.AgendaDAO;
import ulb.infof307.g07.database.dao.agenda.AgendaDefaultDAO;
import ulb.infof307.g07.database.dao.agenda.AgendaMockDAO;
import ulb.infof307.g07.database.dao.home.HomeDAO;
import ulb.infof307.g07.database.dao.home.HomeDefaultDAO;
import ulb.infof307.g07.database.dao.home.HomeMockDAO;
import ulb.infof307.g07.database.dao.ingredient.IngredientDAO;
import ulb.infof307.g07.database.dao.ingredient.IngredientDefaultDAO;
import ulb.infof307.g07.database.dao.ingredient.IngredientMockDAO;
import ulb.infof307.g07.database.dao.menu.MenuDAO;
import ulb.infof307.g07.database.dao.menu.MenuDefaultDAO;
import ulb.infof307.g07.database.dao.menu.MenuMockDAO;
import ulb.infof307.g07.database.dao.product.ProductDAO;
import ulb.infof307.g07.database.dao.product.ProductDefaultDAO;
import ulb.infof307.g07.database.dao.product.ProductMockDAO;
import ulb.infof307.g07.database.dao.recipe.RecipeDAO;
import ulb.infof307.g07.database.dao.recipe.RecipeDefaultDAO;
import ulb.infof307.g07.database.dao.recipe.RecipeMockDAO;
import ulb.infof307.g07.database.dao.shoppinglist.ShoppingListDAO;
import ulb.infof307.g07.database.dao.shoppinglist.ShoppingListDefaultDAO;
import ulb.infof307.g07.database.dao.shoppinglist.ShoppingListMockDAO;
import ulb.infof307.g07.database.dao.store.StoreDAO;
import ulb.infof307.g07.database.dao.store.StoreDefaultDAO;
import ulb.infof307.g07.database.dao.store.StoreMockDAO;
import ulb.infof307.g07.utils.Config;

/**
 * Singleton that contains all the DAOs of the modeles Defaults are used for regular application Mocks are used for
 * testing purposes
 */
public class DAO {
    private static DAO instance = null;

    public static DAO getInstance() {
        if (instance == null) {
            instance = new DAO();
        }
        return instance;
    }

    private IngredientDAO ingredientDAO;
    private RecipeDAO recipeDAO;
    private ShoppingListDAO shoppingListDAO;
    private StoreDAO storeDAO;
    private HomeDAO homeDAO;
    private ProductDAO productDAO;
    private MenuDAO menuDAO;
    private AgendaDAO agendaDAO;

    public IngredientDAO getIngredientDAO() {
        return ingredientDAO;
    }

    public RecipeDAO getRecipeDAO() {
        return recipeDAO;
    }

    public StoreDAO getStoreDAO() {
        return storeDAO;
    }

    public HomeDAO getHomeDAO() {
        return homeDAO;
    }

    public ShoppingListDAO getShoppingListDAO() {
        return shoppingListDAO;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public MenuDAO getMenuDAO() {
        return menuDAO;
    }

    public AgendaDAO getAgendaDAO() {
        return agendaDAO;
    }

    private DAO() {
        switch (Config.getInstance().getEnvironment()) {
        case RUNTIME -> createDefaultDAOs();
        case TESTING -> createMockDAOs();
        }
    }

    private void createDefaultDAOs() {
        ingredientDAO = new IngredientDefaultDAO();
        recipeDAO = new RecipeDefaultDAO();
        shoppingListDAO = new ShoppingListDefaultDAO();
        storeDAO = new StoreDefaultDAO();
        homeDAO = new HomeDefaultDAO();
        productDAO = new ProductDefaultDAO();
        menuDAO = new MenuDefaultDAO();
        agendaDAO = new AgendaDefaultDAO();
    }

    private void createMockDAOs() {
        ingredientDAO = new IngredientMockDAO();
        recipeDAO = new RecipeMockDAO();
        shoppingListDAO = new ShoppingListMockDAO();
        storeDAO = new StoreMockDAO();
        homeDAO = new HomeMockDAO();
        productDAO = new ProductMockDAO();
        menuDAO = new MenuMockDAO();
        agendaDAO = new AgendaMockDAO();
    }
}
