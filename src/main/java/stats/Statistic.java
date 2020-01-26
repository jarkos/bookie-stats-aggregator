package stats;

import lombok.Data;

@Data
class Statistic {
    private int id;
    private double averageOdds1;
    private double averageOdds2;

    public Statistic(int id, double averageOdds1, double averageOdds2) {
        this.id = id;
        this.averageOdds1 = averageOdds1;
        this.averageOdds2 = averageOdds2;
    }
}
