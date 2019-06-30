import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Main {
    final static Logger logger = Logger.getLogger(Main.class.getName());
    private static DataFetcher dataFetcher = new DataFetcher();
    public static ResourceBundle rb = ResourceBundle.getBundle("app");

    public static void main(String[] args) {
        String footballResultsUrl = rb.getString("website.results.football.url");
        dataFetcher.getEventsResultsData(footballResultsUrl);
        System.out.println("Finished Bookie Fetch for: " + LocalDate.now());
    }
}
