package ulb.infof307.g07.utils;

import javafx.scene.paint.Color;

/** Color palette */
public enum Colors {
    ENABLE(Color.valueOf("#4B5563")), DISABLE(Color.valueOf("#9CA3AF")), ERROR(Color.valueOf("#DC2626"));

    private final Color color;

    Colors(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
