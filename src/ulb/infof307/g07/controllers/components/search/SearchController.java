package ulb.infof307.g07.controllers.components.search;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Pair;
import ulb.infof307.g07.controllers.BaseController;
import ulb.infof307.g07.controllers.components.search.popup.*;
import ulb.infof307.g07.managers.scene.SceneManager;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchController implements BaseController {

    @FXML
    private TextField fx_input;

    @FXML
    private HBox fx_filter;

    private final VBox fx_popupmain;

    private OnSearch callback;

    private final Popup popup;

    private List<Filter> filters = new ArrayList<>();

    private String inputData = "";
    private final Map<String, String> filtersData = new HashMap<>();

    public SearchController() {
        popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        fx_popupmain = new PopupController();
        popup.getContent().add(fx_popupmain);
    }

    public void set(OnSearch callback) {
        this.callback = callback;

        fx_filter.setDisable(true);
    }

    /**
     *
     * @param filters
     *            list of filters {@link ulb.infof307.g07.controllers.components.search.Filter}
     * @param callback
     *            called on search
     */
    public void set(List<Filter> filters, OnSearch callback) {
        this.filters = filters;
        this.callback = callback;

        fx_filter.setDisable(filters.size() == 0);
        popup.setOnAutoHide(e -> send());
    }

    private void renderFilters() {
        fx_popupmain.getChildren().clear();
        for (Filter filter : filters) {
            if (filter.getType() == FilterType.INT) {
                addIntFilter(filter.getTitle(), filtersData.get(filter.getTitle()), filter.getUnit());
            } else if (filter.getType() == FilterType.CHECKBOX) {
                addCheckBoxFilter(filter.getTitle(), filtersData.get(filter.getTitle()));
            } else if (filter.getType() == FilterType.CHOICEBOX) {
                addChoiceBoxFilter(filter.getTitle(), filter.getChoices(), filtersData.get(filter.getTitle()),
                        filter.getUnit());
            }
        }
    }

    private void addIntFilter(String title, String value, String unit) {
        PopupIntController popupTextController = new PopupIntController();
        popupTextController.set(title, value, unit, (String txt) -> {
            if (txt.isBlank()) {
                filtersData.remove(title);
            } else {
                filtersData.put(title, txt);
            }
            updateInputFromValues();
        });
        fx_popupmain.getChildren().add(popupTextController);
    }

    private void addChoiceBoxFilter(String title, List<String> choices, String selected, String unit) {
        PopupChoiceboxController popupChoiceboxController = new PopupChoiceboxController();
        popupChoiceboxController.set(title, choices, selected, unit, (String txt) -> {
            if (txt.isBlank()) {
                filtersData.remove(title);
            } else {
                filtersData.put(title, txt);
            }
            updateInputFromValues();
        });
        fx_popupmain.getChildren().add(popupChoiceboxController);
    }

    private void addCheckBoxFilter(String title, String value) {
        PopupCheckboxController popupCheckboxController = new PopupCheckboxController();
        popupCheckboxController.set(title, value, (String state) -> {
            if (Objects.equals(state, State.ON.getState())) {
                filtersData.put(title, state);
            } else {
                filtersData.remove(title);
            }
            updateInputFromValues();
        });
        fx_popupmain.getChildren().add(popupCheckboxController);

    }

    private void updateInputFromValues() {
        StringBuilder inputBuilder = new StringBuilder(inputData == null ? "" : inputData);
        for (Filter filter : filters) {
            String value = filtersData.get(filter.getTitle());
            if (value != null) {
                if (filter.getType() == FilterType.CHOICEBOX) {
                    inputBuilder.append(" ").append(value);
                } else {
                    inputBuilder.append(" ").append(String.format(filter.getRenderFormat(), value));
                }
            }
        }
        String input = inputBuilder.toString();
        input = compressSpaces(input);
        fx_input.setText(input);
    }

    /**
     * update internal states based on input
     */
    private void updateValuesFromInput() {
        String input = fx_input.getText();
        for (Filter filter : filters) {

            if (filter.getType() == FilterType.INT) {
                Pattern regexPattern = Pattern.compile(filter.getMatchFormat(), Pattern.CASE_INSENSITIVE);
                Matcher regexMatcher = regexPattern.matcher(input);
                if (regexMatcher.find()) {
                    String allMatch = regexMatcher.group(0);
                    String extractedValue = regexMatcher.group(1);
                    filtersData.put(filter.getTitle(), extractedValue);
                    input = input.replace(allMatch, "");
                    input = compressSpaces(input);
                }
            } else if (filter.getType() == FilterType.CHECKBOX) {
                String pattern = filter.getRenderFormat();
                Pattern regexPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                Matcher regexMatcher = regexPattern.matcher(input);
                if (regexMatcher.find()) {
                    String allMatch = regexMatcher.group(0);
                    filtersData.put(filter.getTitle(), State.ON.getState());
                    input = input.replace(allMatch, "");
                    input = compressSpaces(input);
                } else {
                    filtersData.remove(filter.getTitle());
                }
            } else if (filter.getType() == FilterType.CHOICEBOX) {
                for (String choice : filter.getChoices()) {
                    Pattern regexPattern = Pattern.compile(choice, Pattern.CASE_INSENSITIVE);
                    Matcher regexMatcher = regexPattern.matcher(input);
                    if (regexMatcher.find()) {
                        String allMatch = regexMatcher.group(0);
                        filtersData.put(filter.getTitle(), choice);
                        input = input.replace(allMatch, "");
                        input = compressSpaces(input);
                        break;
                    } else {
                        filtersData.remove(filter.getTitle());
                    }
                }
            }
        }
        inputData = input;
    }

    private String compressSpaces(String input) {
        return input.trim().replaceAll(" +", " ");
    }

    /**
     * send filter and their value through the callback
     */
    private void send() {
        List<Pair<String, String>> data = new ArrayList<>();
        if (!inputData.isBlank()) {
            data.add(new Pair<>("", inputData));
        }
        for (Map.Entry<String, String> entry : filtersData.entrySet()) {
            data.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        callback.execute(data);
    }

    @FXML
    private void OnInput() {
        updateValuesFromInput();
    }

    @FXML
    private void OnAction() {
        send();
    }

    @FXML
    private void OnSearch() {
        send();
    }

    @FXML
    private void OnFilter(MouseEvent e) {
        renderFilters();
        popup.setX(e.getScreenX());
        popup.setY(e.getScreenY());
        popup.show(SceneManager.getInstance().getMainStage());
        e.consume();
    }

}
