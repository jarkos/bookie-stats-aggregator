package model;

public enum ClickAction {

    TOMORROW("calendar__nav"),
    YESTERDAY("calendar__nav"),
    NONE("none"),
    ;
    public final String value;

    ClickAction(String s) {
        this.value = s;
    }
}
