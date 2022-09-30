package ulb.infof307.g07.database.dao.recipe;

import org.json.JSONObject;
import ulb.infof307.g07.database.Database;
import ulb.infof307.g07.database.DatabaseException;
import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.agenda.exceptions.MenuException;
import ulb.infof307.g07.models.recipe.*;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeDefaultDAO implements RecipeDAO {
    @Override
    public boolean checkRecipeName(String name) throws RecipeException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT id FROM Recipe WHERE name=?;", List.of(name), ResultSet::next);
        } catch (DatabaseException e) {
            throw new RecipeException("Error when checking a recipe name ", e);
        }
    }

    @Override
    public boolean createRecipe(Recipe recipe) throws RecipeException {
        var db = Database.getInstance();

        try {
            if (Menu.getDAO().checkMenuName(recipe.getName())) {
                return false;
            }
            db.begin();
            var currentRecipeId = db.query(
                    "INSERT INTO Recipe(name, number_people, type, instructions, style, time) VALUES(?,?,?,?,?,?);",
                    List.of(recipe.getName(), recipe.getNumberPeople(), recipe.getType().name(),
                            recipe.getInstruction().toString(), recipe.getStyle().name(), recipe.getTime()),
                    Statement.RETURN_GENERATED_KEYS, result -> result.next() ? result.getInt(1) : null);
            int currentIngredientId;
            for (Ingredient ingredient : recipe.getIngredients()) {
                currentIngredientId = getIdFromIngredients(ingredient.getName().toLowerCase(Locale.ROOT));
                if (currentIngredientId == Constants.NOT_A_VALID_ITEM_ID.getValue()) {
                    currentIngredientId = addNewIngredient(ingredient.getName().toLowerCase(Locale.ROOT));
                }
                db.query("INSERT INTO IngredientRecipe(ingredient_id, recipe_id, quantity, unit) VALUES(?,?,?,?);",
                        List.of(currentIngredientId, currentRecipeId, ingredient.getQuantityValue(),
                                ingredient.getUnit().name()));
            }
            // also add in Menu
            db.query("INSERT INTO Menu(name) VALUES(?);", List.of(recipe.getName()));
            db.query("INSERT INTO MenuRecipe(menu_id, recipe_id) VALUES(?,?);",
                    List.of((Menu.getDAO().getIdFromMenu(recipe.getName())), getIdFromRecipe(recipe.getName())));
            db.commit();
            return true;
        } catch (DatabaseException | MenuException e) {
            throw new RecipeException("Error when creating a recipe ", e);
        }
    }

    @Override
    public int addNewIngredient(String ingredient) throws RecipeException {
        var db = Database.getInstance();
        try {
            db.query("INSERT INTO Ingredient(name) VALUES(?);", List.of(ingredient));
            return getIdFromIngredients(ingredient);
        } catch (DatabaseException e) {
            throw new RecipeException("Error when adding a new ingredient ", e);
        }
    }

    @Override
    public int getNumberOfIngredient() throws RecipeException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT COUNT(*) FROM Ingredient;",
                    result -> result.next() ? result.getInt("COUNT(*)") : Constants.NOT_A_VALID_COUNT.getValue());
        } catch (DatabaseException e) {
            throw new RecipeException("Error when getting the number of ingredients ", e);
        }
    }

    @Override
    public Integer getIdFromIngredients(String ingredient) throws RecipeException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT id FROM Ingredient WHERE name=?;", List.of(ingredient),
                    result -> result.next() ? result.getInt("id") : Constants.NOT_A_VALID_ITEM_ID.getValue());
        } catch (DatabaseException e) {
            throw new RecipeException("Error when getting the id from an ingredient ", e);
        }
    }

    @Override
    public ArrayList<Recipe> getAllRecipes() throws RecipeException {
        var db = Database.getInstance();
        var recipesNull = new ArrayList<Recipe>();

        try {
            var ids = new ArrayList<Integer>();
            var recipes = new ArrayList<Recipe>();
            var returnValue = db.query("SELECT * FROM Recipe;", result -> {
                while (result.next()) {
                    ids.add(result.getInt("id"));
                    try {
                        recipes.add(new Recipe(result.getString("name"), result.getInt("number_people"),
                                new ArrayList<>(), new JSONObject(result.getString("instructions")),
                                RecipeStyle.valueOf(result.getString("style")),
                                RecipeType.valueOf(result.getString("type")), result.getInt("time")));
                    } catch (RecipeException e) {
                        return Constants.NOT_A_VALID_ITEM_ID;
                    }
                }
                return recipesNull;
            });
            if (returnValue == Constants.NOT_A_VALID_ITEM_ID)
                throw new RecipeException("Error when adding elements from the database in a recipe object");
            // selecting the ingredients ID
            for (int id : ids) {
                recipes.get(ids.indexOf(id)).setIngredients(getIngredientsFromRecipe(id));
            }
            return recipes;
        } catch (DatabaseException e) {
            throw new RecipeException("Error when getting all recipes ", e);
        }
    }

    @Override
    public String getIngredientNameFromId(int id) throws RecipeException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT name FROM Ingredient WHERE id=?;", List.of(id),
                    result -> result.next() ? result.getString("name") : null);
        } catch (DatabaseException e) {
            throw new RecipeException("Error when getting an ingredient name from an id ", e);
        }
    }

    @Override
    public void deleteRecipeWithId(int id) throws RecipeException {
        var db = Database.getInstance();

        try {
            db.begin();
            Recipe recipe = getRecipe(id);
            db.query("DELETE FROM Recipe WHERE id=?;", List.of(id));
            deleteIngredientRecipe(id);
            assert recipe != null;
            db.commit();
            Menu.getDAO().deleteMenu(Menu.getDAO().getIdFromMenu(recipe.getName()));
        } catch (DatabaseException | MenuException e) {
            throw new RecipeException("Error when deleting a recipe with an id ", e);
        }
    }

    @Override
    public void deleteRecipeWithName(String name) throws RecipeException {
        deleteRecipeWithId(getIdFromRecipe(name));
    }

    @Override
    public void deleteIngredientRecipe(int id) throws RecipeException {
        var db = Database.getInstance();

        try {
            db.query("DELETE FROM IngredientRecipe WHERE recipe_id=?;", List.of((id)));
        } catch (DatabaseException e) {
            throw new RecipeException("Error when deleting an ingredient from a recipe ", e);
        }
    }

    @Override
    public Integer getIdFromRecipe(String name) throws RecipeException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT id FROM Recipe WHERE name=?;", List.of(name),
                    result -> result.next() ? result.getInt("id") : null);
        } catch (DatabaseException e) {
            throw new RecipeException("Error when getting an id from a recipe ", e);
        }
    }

    @Override
    public boolean updateRecipe(Recipe recipe, int id) throws RecipeException {
        var db = Database.getInstance();

        try {
            // Check if this name is already taken or not
            if (checkRecipeName(recipe.getName()) && Menu.getDAO().checkMenuName(recipe.getName())
                    && getIdFromRecipe(recipe.getName()) != id) {
                return false;
            }
            db.begin();
            // Update Recipe
            db.query(
                    "UPDATE Recipe SET name=? , number_people=?, type=?, instructions=?, style=?, time= ?"
                            + " where id=?;",
                    List.of(recipe.getName(), recipe.getNumberPeople(), recipe.getType().name(),
                            recipe.getInstruction().toString(), recipe.getStyle().name(), recipe.getTime(), id));

            db.query("DELETE FROM IngredientRecipe WHERE recipe_id=?;", List.of(id));

            // Insert into RecipeList
            for (Ingredient ingredient : recipe.getIngredients()) {
                int currentIngredientId = getIdFromIngredients(ingredient.getName().toLowerCase(Locale.ROOT));
                if (currentIngredientId == -1) {
                    currentIngredientId = addNewIngredient(ingredient.getName().toLowerCase(Locale.ROOT));
                }
                db.query("INSERT INTO IngredientRecipe(ingredient_id, recipe_id, quantity, unit) VALUES(?,?,?,?);",
                        List.of(currentIngredientId, id, ingredient.getQuantityValue(), ingredient.getUnit().name()));
            }
            // also update Menu name
            db.query("UPDATE Menu SET name=? WHERE id=?;",
                    List.of(recipe.getName(), Menu.getDAO().getIdFromMenu(recipe.getName())));
            db.commit();
            return true;
        } catch (DatabaseException | MenuException e) {
            throw new RecipeException("Error when updating a recipe ", e);
        }
    }

    @Override
    public Recipe getRecipe(String name) throws RecipeException {
        return getRecipe(getIdFromRecipe(name));
    }

    @Override
    public Recipe getRecipe(int id) throws RecipeException {
        var db = Database.getInstance();

        try {
            Recipe recipe = db.query("SELECT * FROM Recipe WHERE id = ?", List.of(id), result -> {
                try {
                    return result.next() ? new Recipe(result.getString("name"), result.getInt("number_people"),
                            getIngredientsFromRecipe(result.getInt("id")),
                            new JSONObject(result.getString("instructions")),
                            RecipeStyle.valueOf(result.getString("style")),
                            RecipeType.valueOf(result.getString("type")), result.getInt("time")) : null;
                } catch (RecipeException e) {
                    return null;
                }
            });
            if (recipe == null)
                throw new RecipeException("Error when creating a recipe with database data from a recipe ID");
            return recipe;
        } catch (DatabaseException e) {
            throw new RecipeException("Error when getting a recipe from the database with an ID", e);
        }
    }

    @Override
    public ArrayList<Ingredient> getIngredientsFromRecipe(String name) throws RecipeException {
        return getIngredientsFromRecipe(getIdFromRecipe(name));
    }

    @Override
    public ArrayList<Ingredient> getIngredientsFromRecipe(int id) throws RecipeException {
        var db = Database.getInstance();
        ArrayList<Ingredient> res = new ArrayList<>();

        try {
            var dbReturn = db.query("SELECT ingredient_id, quantity, unit FROM IngredientRecipe WHERE recipe_id=?;",
                    List.of(id), result -> {
                        while (result.next()) {
                            try {
                                res.add(new Ingredient(getIngredientNameFromId(result.getInt("ingredient_id")),
                                        result.getFloat("quantity"), IngredientUnit.valueOf(result.getString("unit"))));
                            } catch (RecipeException e) {
                                return Constants.NOT_A_VALID_ITEM_ID;
                            }
                        }
                        return res;
                    });
            if (dbReturn == Constants.NOT_A_VALID_ITEM_ID)
                throw new RecipeException("Error when creating an ingredient from an id");
            return res;
        } catch (DatabaseException e) {
            throw new RecipeException("Error when getting an ingredient from a recipe ", e);
        }
    }

    @Override
    public Recipe getSuggestionRecipe(Recipe recipe) throws RecipeException {
        var db = Database.getInstance();

        try {
            Integer id = db.query(
                    "SELECT id FROM Recipe WHERE id NOT IN (SELECT id FROM Recipe "
                            + "WHERE id = ? OR type=\"DESSERT\" OR type=\"DRINK\" OR type=\"ENTREE\")"
                            + "ORDER BY random() LIMIT 1;",
                    List.of(getIdFromRecipe(recipe.getName())), result -> result.next() ? result.getInt("id") : null);
            if (id == null) {
                throw new RecipeException("Failed to retrieve id of the recipe");
            }
            return getRecipe(id);
        } catch (DatabaseException e) {
            throw new RecipeException("Error when getting suggested recipe ", e);
        }
    }

    public enum Constants {
        NOT_A_VALID_ID(0), NOT_A_VALID_ITEM_ID(-1), STARTING_ID(0), NOT_A_VALID_COUNT(-1);

        private final int value;

        Constants(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
