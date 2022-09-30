package ulb.infof307.g07.controllers.agenda;

import ulb.infof307.g07.controllers.agenda.item.MenuAgendaController;
import ulb.infof307.g07.controllers.agenda.item.MenuItemController;
import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.recipe.Recipe;

public class OnAction {

    @FunctionalInterface
    public interface MenuToRecipe {
        void execute(MenuAgendaController mac, Recipe recipe);
    }

    @FunctionalInterface
    public interface PopupMenuToRecipe {
        void execute();
    }

    @FunctionalInterface
    public interface MenuItemDelete {
        void execute(MenuItemController mic);
    }

    @FunctionalInterface
    public interface RecipeInMenuAgendaDelete {
        void execute(int indexOfRecipe);
    }

    @FunctionalInterface
    public interface UpdateMenuController {
        void execute(MenuAgendaController mac);
    }

    @FunctionalInterface
    public interface UpdateMenu {
        void execute(Menu menu);
    }

    @FunctionalInterface
    public interface GenerateAgenda {
        void execute();
    }

}
