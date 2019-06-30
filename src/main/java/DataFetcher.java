import model.ClickAction;
import model.SportEvent;
import model.SportEventRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class DataFetcher {

    private SportEventRepository sportEventRepository = new SportEventRepository();

    void getEventsResultsData(String url) {
        List<SportEvent> allEvents = new ArrayList<>();
        try {
            getEventsResults(url);
        } catch (NumberFormatException nmb) {
            Main.logger.info("NumberFormatException happened");
            nmb.printStackTrace();
        }
        allEvents.forEach(se -> sportEventRepository.saveSportEvents(se));
    }

    private void getEventsResults(String url) {
        String pageContent = RenderPageUtils.renderFullPage(url, ClickAction.TOMORROW);
        Document mainPageYesterday = Jsoup.parse(pageContent);
        var matchesIds = mainPageYesterday.getElementsByClass("event__match event__match--oneLine")
                .stream().map(r -> r.attributes().get("id").substring(4)).collect(Collectors.toSet());
        var yesterdayMatchesInfo = matchesIds.stream().map(id -> RenderPageUtils.renderFullPage(Main.rb.getString("website.results.football.odds.url")
                .replace("ID_HOLDER", id), ClickAction.NONE)).collect(Collectors.toSet());
        var yesterdayMatchesDocuments = yesterdayMatchesInfo.stream().map(Jsoup::parse).collect(Collectors.toSet());
        System.out.println("XD");

        yesterdayMatchesDocuments.forEach(this::fillEvent);
//        Elements eventsRows = resultTable.getElementsByTag("tr");
//        eventsRows.forEach(event -> {
//            Elements columns = event.getElementsByTag("td");
//            String[] teams = splitTeamNames(columns.get(2).text());
//            String[] results = splitEventResult(columns.get(4).text());
//            SportEvent se = fillEvent(columns, teams, results);
//            allEvents.add(se);
//        });
    }

    private SportEvent fillEvent(Document match) {
        SportEvent se = new SportEvent();
//        se.setLeague(columns.get(0).text());
//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        try {
//            Date date = format.parse(columns.get(1).attributes().get("data-dt"));
//            se.setDate(date);
//        } catch (ParseException e) {
//            Main.logger.info("ParseException happened");
//            e.printStackTrace();
//        }
//        se.setFirstTeam(teams[0]);
//        se.setSecondTeam(teams[1]);
//        se.setFirstTeamResult(Integer.parseInt(results[0]));
//        se.setSecondTeamResult(Integer.parseInt(results[1]));
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

}
