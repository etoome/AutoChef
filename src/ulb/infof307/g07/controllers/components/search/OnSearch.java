package ulb.infof307.g07.controllers.components.search;

import java.util.List;

import javafx.util.Pair;

@FunctionalInterface
public interface OnSearch {
    void execute(List<Pair<String, String>> values);
}
