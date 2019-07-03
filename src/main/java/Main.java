import stats.StatsAggregator;

import java.time.LocalDate;
import java.util.ResourceBundle;

public class Main {
    private static DataFetcher dataFetcher = new DataFetcher();
    private static StatsAggregator statsAggregator = new StatsAggregator();
    static ResourceBundle rb = ResourceBundle.getBundle("app");

    public static void main(String[] args) {
        if (Boolean.valueOf(rb.getString("aggregation.data.enabled"))) {
            String footballResultsUrl = rb.getString("website.results.football.url");
            dataFetcher.processEventsResultsData(footballResultsUrl);
        }
        statsAggregator.getStats();
        System.out.println("Finished Bookie Fetch for: " + LocalDate.now());
    }
}
