import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Main {
    final static Logger logger = Logger.getLogger(Main.class.getName());
    private static DataFetcher dataFetcher = new DataFetcher();
    private static ResourceBundle rb = ResourceBundle.getBundle("app");

    public static void main(String[] args) {
        String betsApiFootballUrl = rb.getString("betsapi.site.football.url");
        dataFetcher.getEventsResultsData(betsApiFootballUrl);
        System.out.println("Hello Bookie!");
    }
}
