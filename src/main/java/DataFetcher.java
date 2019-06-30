import model.ClickAction;
import model.Odds;
import model.SportEvent;
import model.SportEventRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

class DataFetcher {

    private SportEventRepository sportEventRepository = new SportEventRepository();

    void getEventsResultsData(String url) {
        try {
            Set<SportEvent> allEvents = getEventsResults(url);
            allEvents.forEach(se -> sportEventRepository.saveSportEvents(se));
        } catch (NumberFormatException nmb) {
            Main.logger.info("NumberFormatException happened");
            nmb.printStackTrace();
        }
    }

    private Set<SportEvent> getEventsResults(String url) {
        String pageContent = RenderPageUtils.renderFullPage(url, ClickAction.TOMORROW);
        Document mainPageYesterday = Jsoup.parse(pageContent);
        List<String> matchesIds = mainPageYesterday.getElementsByClass("event__match event__match--oneLine")
                .stream().map(r -> r.attributes().get("id").substring(4)).collect(Collectors.toList()).subList(0, 3);
        HashMap<String, Document> yesterdayMatchesInfoMap = new HashMap<>();
        matchesIds.forEach(id ->
                yesterdayMatchesInfoMap.put(id, Jsoup.parse(RenderPageUtils.renderFullPage(Main.rb.getString("website.results.football.odds.url")
                        .replace("ID_HOLDER", id), ClickAction.NONE)))
        );
        Set<SportEvent> sportEvents = new HashSet<>();
        yesterdayMatchesInfoMap.forEach((k, v) -> sportEvents.add(fillEvent(k, v)));
        return sportEvents.stream().filter(e -> e.getOdds() != null).collect(Collectors.toSet());
    }

    private SportEvent fillEvent(String id, Document match) {
        SportEvent se = new SportEvent();
        String league = match.childNode(0).childNode(0).childNodes().stream().filter(e -> e.attr("property")
                .equals("og:description")).findFirst().get().attr("content"); //TODO get without check
        String[] title = match.childNode(0).childNode(0).childNodes().stream().filter(e -> e.attr("property")
                .equals("og:title")).findFirst().get().attr("content").split(" "); //TODO get without check
        String[] result = title[3].split(":");

        var timeStamp = match.childNode(0).childNode(0).childNodes().get(76).childNode(0).toString()
                .split("var game_utime")[1].substring(3, 13);
        se.setFlashScoreEventId(id);
        se.setLeague(league);
        Date date = Date.from(Instant.ofEpochSecond(Long.parseLong(timeStamp)));
        se.setDate(date);
        se.setFirstTeam(title[0]);
        se.setSecondTeam(title[2]);
        se.setFirstTeamResult(Integer.parseInt(result[0]));
        se.setSecondTeamResult(Integer.parseInt(result[1]));
        Odds o = fetchOdds(match);
        se.setOdds(o);
        return se;
    }

    private Odds fetchOdds(Document match) {
        HashMap<String, List<String>> oddsMap = new HashMap<>();
        match.getElementById("block-1x2-ft").getElementsByClass("kx").forEach(element -> oddsMap
                .computeIfAbsent(element.attributes().get("onclick").split("ft_")[1].substring(0, 1), k -> new ArrayList<>()).add(element.text()));
        Odds o = new Odds();
        oddsMap.forEach((k, v) -> {
            if (k.equals("0")) {
                o.setBookieA_0_odds(Double.parseDouble(v.get(0)));
                o.setBookieB_0_odds(Double.parseDouble(v.get(1)));
                o.setBookieC_0_odds(Double.parseDouble(v.get(2)));
                o.setBookieD_0_odds(Double.parseDouble(v.get(3)));
            } else if (k.equals("1")) {
                o.setBookieA_1_odds(Double.parseDouble(v.get(0)));
                o.setBookieB_1_odds(Double.parseDouble(v.get(1)));
                o.setBookieC_1_odds(Double.parseDouble(v.get(2)));
                o.setBookieD_1_odds(Double.parseDouble(v.get(3)));
            } else {
                o.setBookieA_2_odds(Double.parseDouble(v.get(0)));
                o.setBookieB_2_odds(Double.parseDouble(v.get(1)));
                o.setBookieC_2_odds(Double.parseDouble(v.get(2)));
                o.setBookieD_2_odds(Double.parseDouble(v.get(3)));
            }
        });
        return o;
    }
}
