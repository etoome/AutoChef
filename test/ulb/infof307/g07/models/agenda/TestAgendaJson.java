package ulb.infof307.g07.models.agenda;

import org.junit.jupiter.api.*;
import ulb.infof307.g07.models.recipe.RecipeStyle;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestAgendaJson {

    Map<RecipeStyle, Integer> agendaSettings;
    int numberRecipeDay;

    @BeforeEach
    void saveAllSettings() {
        numberRecipeDay = AgendaJson.getInstance().getAgendaNumberDay();
        agendaSettings = AgendaJson.getInstance().getAgendaSettings();
    }

    @Test
    void saveNullSettingsShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> AgendaJson.getInstance().saveAgendaSettings(2, null));
    }

    @Test
    void saveNegativeNumberOfRecipePerDayShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> AgendaJson.getInstance().saveAgendaSettings(-5, new HashMap<>()));
    }

    @Test
    void setNumberRecipeDayWorks() {
        AgendaJson.getInstance().saveAgendaSettings(5, new HashMap<>());
        assertEquals(5, AgendaJson.getInstance().getAgendaNumberDay());
    }

    @Test
    void setSettingsWorks() {
        Map<RecipeStyle, Integer> settingsBefore = new HashMap<>();
        settingsBefore.put(RecipeStyle.MEAT, 9);
        AgendaJson.getInstance().saveAgendaSettings(9, settingsBefore);
        assertEquals(9, AgendaJson.getInstance().getAgendaSettings().get(RecipeStyle.MEAT));
    }

    @AfterEach
    void saveOldValueSettings() {
        AgendaJson.getInstance().saveAgendaSettings(numberRecipeDay, agendaSettings);
    }
}
