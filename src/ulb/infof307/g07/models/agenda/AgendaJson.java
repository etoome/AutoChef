package ulb.infof307.g07.models.agenda;

import org.json.JSONException;
import org.json.JSONObject;
import ulb.infof307.g07.models.recipe.*;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Save preference from the agenda hebdomadaire locally
 */
public class AgendaJson {

    private static final String PREF_JSON = "agenda_settings";
    private static final String NUMBER_DAY_STRING = "numberDay";
    private static AgendaJson instance;

    public static AgendaJson getInstance() {
        if (instance == null) {
            instance = new AgendaJson();
        }
        return instance;
    }

    private AgendaJson() {
    }

    /**
     * Get agenda hebdomadaire style settings from the user preference, list of recipeStyle with the number of each
     */
    public Map<RecipeStyle, Integer> getAgendaSettings() {
        Map<RecipeStyle, Integer> settings = new HashMap<>();
        Preferences pref = Preferences.userNodeForPackage(AgendaJson.class);
        JSONObject settingsJson = new JSONObject(pref.get(PREF_JSON, "{}"));
        for (RecipeStyle rs : RecipeStyle.values()) {
            if (!settingsJson.isNull(rs.toString())) {
                settings.put(rs, settingsJson.getInt(rs.toString()));
            } else {
                settings.put(rs, 0);
            }
        }
        return settings;
    }

    /**
     * Get agenda hebdomadaire number per day from the user preference, int of the number of recipe per day
     *
     * @return int of the number of recipe per day
     */
    public int getAgendaNumberDay() throws JSONException {
        Preferences pref = Preferences.userNodeForPackage(AgendaJson.class);
        JSONObject settingsJson = new JSONObject(pref.get(PREF_JSON, "{" + NUMBER_DAY_STRING + ": 0}"));
        return settingsJson.getInt(NUMBER_DAY_STRING);
    }

    /**
     * Save all settings from the agenda hebdomadaire, using Preferences.userNodeForPackage, to save it locally
     *
     * @param nbDay:
     *            number of recipe per day
     * @param settings:
     *            recipeStyle list & quantity of each style
     */
    public void saveAgendaSettings(int nbDay, Map<RecipeStyle, Integer> settings) throws IllegalArgumentException {
        if (nbDay < 0)
            throw new IllegalArgumentException("Can't set a negative number of recipe per day in AgendaJson");
        if (settings == null)
            throw new IllegalArgumentException("Can't set null settings in AgendaJson");
        Preferences pref = Preferences.userNodeForPackage(AgendaJson.class);
        JSONObject recipeJson = new JSONObject();
        recipeJson.put(NUMBER_DAY_STRING, nbDay);
        for (RecipeStyle style : settings.keySet()) {
            recipeJson.put(style.toString(), settings.get(style));
        }
        pref.put(PREF_JSON, recipeJson.toString());
    }

}
