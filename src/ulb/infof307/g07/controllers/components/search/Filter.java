package ulb.infof307.g07.controllers.components.search;

import java.util.List;

public class Filter {
    private final String title;
    private final FilterType type;
    private List<String> choices;
    private String renderFormat;
    private String matchFormat;
    private String unit;

    /**
     * @param type
     *            {@link ulb.infof307.g07.controllers.components.search.FilterType}
     * @param renderFormat
     *            in java format
     * @param matchFormat
     *            regex to match strings belonging to the filter
     */
    public Filter(String title, FilterType type, String renderFormat, String matchFormat)
            throws IllegalArgumentException {
        if (type == FilterType.CHOICEBOX)
            throw new IllegalArgumentException("Le type ne peut pas etre un CHOICEBOX");

        this.title = title;
        this.type = type;
        this.renderFormat = renderFormat;
        this.matchFormat = matchFormat;
    }

    /**
     * constructor with units
     *
     * @see ulb.infof307.g07.controllers.components.search.Filter
     */
    public Filter(String title, FilterType type, String renderFormat, String matchFormat, String unit)
            throws IllegalArgumentException {
        this(title, type, renderFormat, matchFormat);

        this.unit = unit;
    }

    /**
     * constructor for CHECKBOX type
     *
     * @see ulb.infof307.g07.controllers.components.search.Filter
     */
    public Filter(String title, FilterType type, String renderFormat) throws IllegalArgumentException {
        if (type != FilterType.CHECKBOX)
            throw new IllegalArgumentException("Le type doit etre un CHECKBOX");

        this.title = title;
        this.type = type;
        this.renderFormat = renderFormat;
        this.matchFormat = renderFormat;
    }

    /**
     * constructor for CHOICEBOX type
     *
     * @see ulb.infof307.g07.controllers.components.search.Filter
     */
    public Filter(String title, FilterType type, List<String> choices) throws IllegalArgumentException {
        if (type != FilterType.CHOICEBOX)
            throw new IllegalArgumentException("Le type doit etre un CHOICEBOX");

        this.title = title;
        this.type = type;
        this.choices = choices;
    }

    /**
     * constructor for CHOICEBOX type with unit
     *
     * @see ulb.infof307.g07.controllers.components.search.Filter
     */
    public Filter(String title, FilterType type, List<String> choices, String unit) throws IllegalArgumentException {
        this(title, type, choices);

        this.unit = unit;
    }

    public String getTitle() {
        return title;
    }

    public FilterType getType() {
        return type;
    }

    public List<String> getChoices() {
        return choices;
    }

    /**
     * @return render string in java format
     */
    public String getRenderFormat() {
        return renderFormat;
    }

    /**
     * @return match string in regex format
     */
    public String getMatchFormat() {
        return matchFormat;
    }

    public String getUnit() {
        return unit;
    }
}
