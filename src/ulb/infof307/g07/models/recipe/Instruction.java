package ulb.infof307.g07.models.recipe;

/** Key-value pair that represents an instruction for the recipe */
public class Instruction {

    private int key;
    private String value;

    public Instruction(int key, String value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null!");
        }
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null!");
        }
        this.value = value;
    }
}
