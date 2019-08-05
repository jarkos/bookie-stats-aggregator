package model;

public enum EventType {
    FOOTBALL("block-1x2-ft", "ft_"),
    TENIS("block-moneyline-ft", "ft_"),
    BASKETBALL("block-moneyline", "ot_");

    public final String resultBlockId;
    public final String splitResultBlockString;

    EventType(String s, String index) {
        this.resultBlockId = s;
        this.splitResultBlockString = index;

    }
}
