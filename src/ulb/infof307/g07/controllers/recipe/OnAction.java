package ulb.infof307.g07.controllers.recipe;

import ulb.infof307.g07.models.recipe.Recipe;

public class OnAction {

    @FunctionalInterface
    public interface OnIngredientDelete {
        void execute();
    }

    @FunctionalInterface
    public interface OnInstructionDelete {
        void execute();
    }

    @FunctionalInterface
    public interface OnRecipeClick {
        void execute(Recipe recipe);
    }

    @FunctionalInterface
    public interface OnRecipeCreate {
        void execute(Recipe recipe);
    }

    @FunctionalInterface
    public interface OnRecipeDelete {
        void execute(Recipe recipe);
    }

    @FunctionalInterface
    public interface OnRecipeUpdate {
        void execute();
    }

    @FunctionalInterface
    public interface OnSwitchViewEdit {

        void execute(Recipe recipe);

    }
}
