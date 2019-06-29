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
import java.util.stream.IntStream;

class DataFetcher {

    private SportEventRepository sportEventRepository = new SportEventRepository();

    void getEventsResultsData(String url) {
        List<SportEvent> allEvents = new ArrayList<>();
        try {
            IntStream i = IntStream.range(0, 21);
            i.forEach(pageNumber -> {
                try {
                    getEventsResultPageNumber(url, allEvents, pageNumber);
                } catch (IOException e) {
                    Main.logger.info("IOException happened");
                    e.printStackTrace();
                }
            });
        } catch (NumberFormatException nmb) {
            Main.logger.info("NumberFormatException happened");
            nmb.printStackTrace();
        }
        allEvents.forEach(se -> sportEventRepository.saveSportEvents(se));
    }

    private void getEventsResultPageNumber(String url, List<SportEvent> allEvents, int pageNumber) throws IOException {
        if (pageNumber != 0) {
            url += "p." + pageNumber;
        }
        Document doc = Jsoup.connect(url).get();
        Element resultTable = doc.getElementsByClass("table table-sm").first();
        Elements eventsRows = resultTable.getElementsByTag("tr");
        eventsRows.forEach(event -> {
            Elements columns = event.getElementsByTag("td");
            String[] teams = splitTeamNames(columns.get(2).text());
            String[] results = splitEventResult(columns.get(4).text());
//                String coreUrl = url.substring(0, 20);
//                String oddsUrl = coreUrl + columns.get(4).childNode(1).attributes()
//                        .get("href").replace("/r/", "rs/");
//                Double odds = getOdds(oddsUrl);
            SportEvent se = fillEvent(columns, teams, results);
            allEvents.add(se);
        });
    }

    private SportEvent fillEvent(Elements columns, String[] teams, String[] results) {
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
        return se;
    }

    private Double getOdds(String oddsUrl) {
        Document doc = null;
        try {
            doc = Jsoup.connect(oddsUrl).get();
        } catch (IOException e) {
            Main.logger.info("IOException happened for odds fetch");
            e.printStackTrace();
        }
        assert doc != null;
//        Element resultTable = doc.getElementsByClass("row").first();
//        Element resultTable2 = doc.getElementsByClass("table table-sm table-bordered").first();
//        if (resultTable != null) {
//
//        }
        return null;
    }

    private String[] splitTeamNames(String toSplit) {
        return toSplit.split(" v ");
    }

    private String[] splitEventResult(String toSplit) {
        return toSplit.split("-");
    }

}
