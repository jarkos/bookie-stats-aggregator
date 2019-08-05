public enum ClickAction {

    TOMORROW("calendar__direction--yesterday"),
    NONE("none"),
    ;
    public final String value;

    ClickAction(String s) {
        this.value = s;
    }
}
