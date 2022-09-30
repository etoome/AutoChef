package ulb.infof307.g07.controllers.components;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import ulb.infof307.g07.controllers.BaseController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListEditableController implements BaseController {

    @FXML
    private SVGPath fx_add;

    @FXML
    private HBox fx_add_container;

    @FXML
    private SVGPath fx_delete;

    @FXML
    private HBox fx_delete_container;

    @FXML
    private ComboBox<Object> fx_list;

    public <T> void start(List<T> initialElements, OnListSelect<T> callbackSelect, OnListEdit<T> callbackEdit,
            OnListAdd<T> callbackAdd, OnListDelete<T> callbackDelete) {

        fx_list.getItems().addAll(initialElements);

        if (!fx_list.getItems().isEmpty()) {
            fx_list.getSelectionModel().selectFirst();
            Object selectedObject = fx_list.getSelectionModel().getSelectedItem();
            // noinspection unchecked
            T selected = (T) selectedObject;
            callbackSelect.execute(selected);
        }

        fx_list.setOnAction(event -> {
            if (fx_list.getSelectionModel().isEmpty()) {

                String editorText = fx_list.getEditor().getText();

                Pattern pattern = Pattern.compile("^([\\w\\d-_]+\\s*)+$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(editorText);
                boolean validFormat = matcher.find();

                if (validFormat && callbackEdit != null) {
                    List<T> elements = callbackEdit.execute(editorText);
                    fx_list.getItems().clear();
                    fx_list.getItems().addAll(elements);
                }

            } else {
                if (callbackSelect != null) {
                    Object selectedObject = fx_list.getSelectionModel().getSelectedItem();
                    // noinspection unchecked
                    T selected = (T) selectedObject;
                    callbackSelect.execute(selected);
                }
            }
            event.consume();
        });

        fx_add_container.setOnMouseClicked(event -> {
            if (callbackAdd != null) {
                List<T> elements = callbackAdd.execute();
                fx_list.getItems().clear();
                if (!elements.isEmpty()) {
                    fx_list.getItems().addAll(elements);
                    fx_list.getSelectionModel().select(elements.get(elements.size() - 1));
                }
            }
            event.consume();
        });

        fx_delete_container.setOnMouseClicked(event -> {
            if (callbackDelete != null) {
                List<T> elements = callbackDelete.execute();
                fx_list.getItems().clear();
                fx_list.getSelectionModel().clearSelection();
                if (!elements.isEmpty()) {
                    fx_list.getItems().addAll(elements);
                }
                if (!fx_list.getItems().isEmpty())
                    fx_list.setValue(fx_list.getItems().get(0));
            }
            event.consume();
        });
    }

    /**
     * refresh the current selection
     */
    public void refresh() {
        int selected = fx_list.getSelectionModel().getSelectedIndex();
        fx_list.getSelectionModel().clearSelection();
        fx_list.getSelectionModel().select(selected);
    }

    public void setListValue(Object item) {
        fx_list.setValue(item);
    }

    public <T> List<T> getList() {
        ArrayList<T> convertedList = new ArrayList<>();
        for (Object object : fx_list.getItems()) {
            // noinspection unchecked
            convertedList.add((T) object);
        }
        return convertedList;
    }

    public String getWrittenText() {
        return fx_list.getEditor().getText();
    }
}
