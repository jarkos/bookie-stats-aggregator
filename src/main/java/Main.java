import model.EventType;
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
            String tennisResultsUrl = rb.getString("website.results.tennis.url");
            String matchOddsResultsUrl = Main.rb.getString("website.results.match.odds.url");
            dataFetcher.processEventsResultsData(footballResultsUrl, matchOddsResultsUrl, EventType.FOOTBALL);
            dataFetcher.processEventsResultsData(tennisResultsUrl, matchOddsResultsUrl, EventType.TENIS);
        }
        statsAggregator.getRoiStats();
        System.out.println("Finished Bookie stats fetch for: " + LocalDate.now());
    }
}
