import lombok.AllArgsConstructor;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DataFetcher {

    void getEventsResultsData(String url) {
        List<EventTableRow> allEvents = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Element resultTable = doc.getElementsByClass("table table-sm").first();
            Elements eventsRows = resultTable.getElementsByTag("tr");
            eventsRows.forEach(event -> {
                Elements columns = event.getElementsByTag("td");
                String[] teams = splitTeamNames(columns.get(2).text());
                String[] results = splitEventResult(columns.get(4).text());
                allEvents.add(
                        new EventTableRow(columns.get(0).text(), columns.get(1).attributes().get("data-dt"),
                                teams[0], teams[1], Integer.parseInt(results[0]), Integer.parseInt(results[1])));
            });
        } catch (java.lang.NumberFormatException nmb) {
            Main.logger.info("NumberFormatException happened");
            nmb.printStackTrace();
        } catch (IOException ioe) {
            Main.logger.info("IOException happened");
            ioe.printStackTrace();
        }

    }

    private String[] splitTeamNames(String toSplit) {
        return toSplit.split(" v ");
    }

    private String[] splitEventResult(String toSplit) {
        return toSplit.split("-");
    }

    @Data
    @AllArgsConstructor
    private class EventTableRow {
        private String league;
        private String data;
        private String firstTeam;
        private String secondTeam;
        private int firstTeamResult;
        private int secondTeamResult;
//        private Double odd;
    }
}
