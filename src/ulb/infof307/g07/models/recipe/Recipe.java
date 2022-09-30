package ulb.infof307.g07.models.recipe;

import org.json.JSONObject;
import ulb.infof307.g07.database.dao.DAO;
import ulb.infof307.g07.database.dao.recipe.RecipeDAO;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;

import java.util.ArrayList;
import java.util.List;

/**
 * The recipe model, has a name, number of people, time that the recipe take, the type of recipe (RecipeType), list of
 * ingredients (Ingredient objects), the style of the recipe (RecipeStyle), list of instructions (JSON object).
 */
public class Recipe {
    private String name;
    private Integer numberPeople;
    private Integer time;
    private RecipeType type;
    private ArrayList<Ingredient> ingredients;
    private RecipeStyle style;
    private JSONObject instruction;

    private static final int DEFAULT_INITIALIZATION = 1;

    /**
     * Create a recipe object with all default values
     *
     * @param name:
     *            the name none null of the recipe
     */
    public Recipe(String name) throws RecipeException {
        this(name, DEFAULT_INITIALIZATION, new ArrayList<>(), new JSONObject("{\"0\":\"\"}"), RecipeStyle.MEAT,
                RecipeType.MEAL, DEFAULT_INITIALIZATION);
    }

    /**
     * Create a recipe object with all values who need to be set and non-null
     */
    public Recipe(String name, Integer numberPeople, List<Ingredient> ingredients, JSONObject instruction,
            RecipeStyle style, RecipeType type, Integer time) throws RecipeException {
        if (name == null) {
            throw new IllegalArgumentException("Can't make an empty name to the recipe");
        }
        if (numberPeople <= 0) {
            throw new RecipeException("Can't make a recipe with less than 1 person");
        }
        if (ingredients == null) {
            throw new IllegalArgumentException("Can't make a recipe with no ingredients");
        }
        if (time < 0) {
            throw new RecipeException("Can't make a recipe with negative time");
        }
        if (instruction.length() < 0) {
            throw new RecipeException("Can't have negative length instructions");
        }
        this.name = name;
        this.numberPeople = numberPeople;
        this.ingredients = (ArrayList<Ingredient>) ingredients;
        this.instruction = instruction;
        this.style = style;
        this.type = type;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Can't set a null name to a recipe");
        this.name = name;
    }

    public Integer getNumberPeople() {
        return numberPeople;
    }

    public void setNumberPeople(Integer numberPeople) throws RecipeException {
        if (numberPeople == null)
            throw new IllegalArgumentException("Can't set a null number of people for a recipe");
        if (numberPeople < 0)
            throw new RecipeException("Can't set an invalid number of people for a recipe");
        this.numberPeople = numberPeople;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        if (ingredients == null)
            throw new IllegalArgumentException("Can't set a null list of ingredients for a recipe");
        this.ingredients = (ArrayList<Ingredient>) ingredients;
    }

    public JSONObject getInstruction() {
        return instruction;
    }

    public void setInstruction(JSONObject instruction) throws RecipeException {
        if (instruction == null)
            throw new IllegalArgumentException("Can't set a null instruction for a recipe");
        if (instruction.length() < 0)
            throw new RecipeException("Can't set empty instructions for a recipe");

        this.instruction = instruction;
    }

    public RecipeStyle getStyle() {
        return style;
    }

    public void setStyle(RecipeStyle style) {
        if (style == null)
            throw new IllegalArgumentException("can't set null recipe style to a recipe");
        this.style = style;
    }

    public RecipeType getType() {
        return type;
    }

    public void setType(RecipeType type) {
        if (type == null)
            throw new IllegalArgumentException("Can't set null recipe type to a recipe");
        this.type = type;
    }

    public java.lang.Integer getTime() {
        return this.time;
    }

    public void setTime(java.lang.Integer time) throws RecipeException {
        if (time <= 0) {
            throw new RecipeException("Can't set a negative time for a recipe");
        }
        this.time = time;
    }

    public static RecipeDAO getDAO() {
        return DAO.getInstance().getRecipeDAO();
    }
}