import model.SportEvent;
import model.SportEventRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class DataFetcher {

    private SportEventRepository sportEventRepository = new SportEventRepository();

    void getEventsResultsData(String url) {
        List<SportEvent> allEvents = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Element resultTable = doc.getElementsByClass("table table-sm").first();
            Elements eventsRows = resultTable.getElementsByTag("tr");
            eventsRows.forEach(event -> {
                Elements columns = event.getElementsByTag("td");
                String[] teams = splitTeamNames(columns.get(2).text());
                String[] results = splitEventResult(columns.get(4).text());
                SportEvent se = new SportEvent();
                se.setLeague(columns.get(0).text());
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    Date date = format.parse(columns.get(1).attributes().get("data-dt"));
                    se.setDate(date);
                } catch (ParseException e) {
                    Main.logger.info("ParseException happened");
                    e.printStackTrace();
                }
                se.setFirstTeam(teams[0]);
                se.setSecondTeam(teams[1]);
                se.setFirstTeamResult(Integer.parseInt(results[0]));
                se.setSecondTeamResult(Integer.parseInt(results[1]));
                allEvents.add(se);

            });
        } catch (java.lang.NumberFormatException nmb) {
            Main.logger.info("NumberFormatException happened");
            nmb.printStackTrace();
        } catch (IOException ioe) {
            Main.logger.info("IOException happened");
            ioe.printStackTrace();
        }
        allEvents.forEach(se -> sportEventRepository.saveSportEvents(se));
    }

    private String[] splitTeamNames(String toSplit) {
        return toSplit.split(" v ");
    }

    private String[] splitEventResult(String toSplit) {
        return toSplit.split("-");
    }

}
