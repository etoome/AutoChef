package ulb.infof307.g07.models.store;

/** Represents a position on the map */
public record Position(double lat, double lng) {

    public boolean equals(Position other) {
        if (other == null)
            return false;
        return lng() == other.lng() && lat() == other.lat();
    }

    /** @return get the Euclidean distance between two positions */
    public Double distanceAbsolute(Position other) {
        if (other == null) {
            throw new IllegalArgumentException("Can't take distance with a null param");
        }

        double vlng = other.lng - this.lng;
        double vlat = other.lat - this.lat;
        return Math.sqrt(vlng * vlng + vlat * vlat);
    }

    /** @return Create a new position between (x1, y1) and (x2, y2) */
    static public Position generateRandomPositionBetween(double x1, double x2, double y1, double y2) {
        if (x1 > x2 || y1 > y2) {
            throw new IllegalArgumentException(
                    "Use valid range (x1 and y1 must be smaller than x2 and y2, respectively");
        }
        return new Position(Math.random() % x2 + x1, Math.random() % y2 + y1);
    }

}
