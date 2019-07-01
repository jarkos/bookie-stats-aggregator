import java.time.LocalDate;
import java.util.ResourceBundle;

public class Main {
    private static DataFetcher dataFetcher = new DataFetcher();
    public static ResourceBundle rb = ResourceBundle.getBundle("app");

    public static void main(String[] args) {
        String footballResultsUrl = rb.getString("website.results.football.url");
        dataFetcher.processEventsResultsData(footballResultsUrl);
        System.out.println("Finished Bookie Fetch for: " + LocalDate.now());
    }
}
