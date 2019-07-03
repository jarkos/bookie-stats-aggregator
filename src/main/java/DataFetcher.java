import lombok.extern.log4j.Log4j2;
import model.EventType;
import model.Odds;
import model.SportEvent;
import model.SportEventRepository;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
class DataFetcher {

    private SportEventRepository sportEventRepository = new SportEventRepository();

    void processEventsResultsData(String url) {
        getEventsResultsAndSave(url, EventType.FOOTBALL);
    }

    private void getEventsResultsAndSave(String url, EventType type) {
        String pageContent = RenderPageUtils.renderFullPage(url, ClickAction.TOMORROW);
        Document mainPageYesterday = Jsoup.parse(pageContent);
        List<String> matchesIds = getMatchesFlashscoreIds(mainPageYesterday);
        matchesIds.forEach(id -> {
                    if (!sportEventRepository.checkEventExist(id)) {
                        var doc = Jsoup.parse(RenderPageUtils.renderFullPage(Main.rb.getString("website.results.football.odds.url")
                                .replace("ID_HOLDER", id), ClickAction.NONE));
                        try {
                            SportEvent sportEvent = fillSportEvent(id, type, doc);
                            sportEventRepository.saveSportEvents(sportEvent);
                        } catch (NumberFormatException nmb) {
                            log.warn("NumberFormatException happened, id: " + id);
                            nmb.printStackTrace();
                        } catch (MatchCancelledException e) {
                            log.info("Match with id :" + id + " was canceled");
                        } catch (Exception e) {
                            log.error("Exception on filling event with id: " + id, e);
                        }
                    } else {
                        log.info("Event already exist, id: " + id);
                    }
                }
        );
    }

    private List<String> getMatchesFlashscoreIds(Document mainPageYesterday) {
        return mainPageYesterday.getElementsByClass("event__match event__match--oneLine")
                .stream().filter(e -> !e.getElementsByClass("odds__odd icon icon--arrow kx no-odds null null   ").text().equals("- -"))
                .map(r -> r.attributes().get("id").substring(4)).collect(Collectors.toList());
    }

    private SportEvent fillSportEvent(String id, EventType type, Document match) throws MatchCancelledException {
        SportEvent se = new SportEvent();
        String league = match.childNode(0).childNode(0).childNodes().stream().filter(e -> e.attr("property")
                .equals("og:description")).findFirst().get().attr("content"); //TODO get without check
        match.getElementById("utime");
        Date date = null;
        if (StringUtils.isNotBlank(match.getElementById("utime").text())) {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            try {
                date = format.parse(match.getElementById("utime").text().replace(".", "-"));
            } catch (ParseException e) {
                log.error("ParseException happened, id: " + id);
            }
        } else {
            Instant instant = Instant.now();
            long timeStampMillis = instant.toEpochMilli();
            date = Date.from(Instant.ofEpochSecond(timeStampMillis));
        }
        se.setFlashScoreEventId(id);
        se.setType(type);
        se.setLeague(league);
        se.setDate(date);
        fillResult(match, se);
        se.setFirstTeam(match.getElementsByClass("participant-imglink").get(1).text());
        se.setSecondTeam(match.getElementsByClass("participant-imglink").get(3).text());
        Odds o = fetchOdds(match);
        se.setOdds(o);
        return se;
    }

    private void fillResult(Document match, SportEvent se) throws MatchCancelledException {
        String[] result;
        if (match.getElementsByClass("current-result").text().length() > 7) {
            result = match.getElementsByClass("current-result").text().split(" ")[1]
                    .replace("(", "").replace(")", "").split("-");
        } else if (match.getElementsByClass("current-result").text().equals("-")) {
            throw new MatchCancelledException();
        } else {
            result = match.getElementsByClass("current-result").text().split(" ")[0].split("-");
        }
        se.setFirstTeamResult(Integer.parseInt(result[0]));
        se.setSecondTeamResult(Integer.parseInt(result[1]));
    }

    private Odds fetchOdds(Document match) {
        Odds o = new Odds();
        HashMap<String, List<String>> oddsMap = new HashMap<>();
        if (match.getElementById("block-1x2-ft") != null) {
            match.getElementById("block-1x2-ft").getElementsByClass("kx").forEach(element -> oddsMap
                    .computeIfAbsent(element.attributes().get("onclick").split("ft_")[1].substring(0, 1), k -> new ArrayList<>()).add(element.text()));

            oddsMap.forEach((k, v) -> {
                if (k.equals("0")) {
                    o.setBookieA_0_odds(parseToDouble(v.get(0)));
                    if (v.size() > 1) {
                        o.setBookieB_0_odds(parseToDouble(v.get(1)));
                    }
                    if (v.size() > 2) {
                        o.setBookieC_0_odds(parseToDouble(v.get(2)));
                    }
                    if (v.size() > 3) {
                        o.setBookieD_0_odds(parseToDouble(v.get(3)));
                    }
                } else if (k.equals("1")) {
                    o.setBookieA_1_odds(parseToDouble(v.get(0)));
                    if (v.size() > 1) {
                        o.setBookieB_1_odds(parseToDouble(v.get(1)));
                    }
                    if (v.size() > 2) {
                        o.setBookieC_1_odds(parseToDouble(v.get(2)));
                    }
                    if (v.size() > 3) {
                        o.setBookieD_1_odds(parseToDouble(v.get(3)));
                    }
                } else {
                    o.setBookieA_2_odds(parseToDouble(v.get(0)));
                    if (v.size() > 1) {
                        o.setBookieB_2_odds(parseToDouble(v.get(1)));
                    }
                    if (v.size() > 2) {
                        o.setBookieC_2_odds(parseToDouble(v.get(2)));
                    }
                    if (v.size() > 3) {
                        o.setBookieD_2_odds(parseToDouble(v.get(3)));
                    }
                }
            });
        }
        return o;
    }

    private double parseToDouble(String v) {
        if (v.equals("-")) {
            return 0.0;
        } else {
            return Double.parseDouble(v);
        }
    }
}
