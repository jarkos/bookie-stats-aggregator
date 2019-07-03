package stats;

import lombok.extern.log4j.Log4j2;
import model.Odds;
import model.OddsRepository;
import model.SportEvent;
import model.SportEventRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
public class StatsAggregator {

    private OddsRepository oddsRepository = new OddsRepository();
    private SportEventRepository sportEventsRepository = new SportEventRepository();

    public void getStats() {
        countSuccessRateForOddsLesThan(1.55);
        countSuccessRateForOddsLesThan(1.45);
        countSuccessRateForOddsLesThan(1.35);
        countSuccessRateForOddsLesThan(1.25);
        countSuccessRateForOddsLesThan(1.15);
        countSuccessRateForOddsLesThan(1.05);
    }

    private void countSuccessRateForOddsLesThan(double oddsLessThanValue) {
        List<Odds> allOds = oddsRepository.getAllOddsLessThanValue(oddsLessThanValue);
        var allStatisticWithAverageOdds = allOds.stream().map(this::mapToStatistic).collect(Collectors.toList());
        AtomicInteger successfulPredictionResultCount = new AtomicInteger();
        double allStatsSize = allStatisticWithAverageOdds.size();
        allStatisticWithAverageOdds.forEach(stat -> {
            SportEvent se = sportEventsRepository.getSportEventByOddsId(stat.getId());
            boolean check;
            if (stat.getAverageOdds1() >= 1.0) {
                check = se.getFirstTeamResult() > se.getSecondTeamResult();
            } else {
                check = se.getFirstTeamResult() < se.getSecondTeamResult();
            }
            if (check) {
                successfulPredictionResultCount.getAndIncrement();
            }
        });
        log.info("Result for odds " + oddsLessThanValue + ": " + successfulPredictionResultCount.doubleValue() / allStatsSize);
    }

    private Statistic mapToStatistic(Odds o) {
        HashMap<String, List<Double>> oddsMap = new HashMap<>();
        oddsMap.put("1", new ArrayList<>());
        oddsMap.put("2", new ArrayList<>());

        if ((o.getBookieA_1_odds() < 1.55 && o.getBookieA_1_odds() > 0) || (o.getBookieB_1_odds() < 1.55 && o.getBookieB_1_odds() > 0)
                || (o.getBookieC_1_odds() < 1.55 && o.getBookieC_1_odds() > 0) || (o.getBookieD_1_odds() < 1.55 && o.getBookieD_1_odds() > 0)) {
            ArrayList<Double> list = new ArrayList<>();
            list.add(o.getBookieA_1_odds());
            list.add(o.getBookieB_1_odds());
            list.add(o.getBookieC_1_odds());
            list.add(o.getBookieD_1_odds());
            oddsMap.put("1", list);
        } else if ((o.getBookieA_2_odds() < 1.55 && o.getBookieA_2_odds() > 0) || (o.getBookieB_2_odds() < 1.55 && o.getBookieB_2_odds() > 0)
                || (o.getBookieC_2_odds() < 1.55 && o.getBookieC_2_odds() > 0) || (o.getBookieD_2_odds() < 1.55 && o.getBookieD_2_odds() > 0)) {
            ArrayList<Double> list = new ArrayList<>();
            list.add(o.getBookieA_2_odds());
            list.add(o.getBookieB_2_odds());
            list.add(o.getBookieC_2_odds());
            list.add(o.getBookieD_2_odds());
            oddsMap.put("2", list);
        }
        double a = 0, b = 0;
        if (!oddsMap.get("1").isEmpty()) {
            a = oddsMap.get("1").stream().filter(x -> !x.equals(0.0)).mapToDouble(Double::doubleValue).average().getAsDouble();
        }
        if (!oddsMap.get("2").isEmpty()) {
            b = oddsMap.get("2").stream().filter(x -> !x.equals(0.0)).mapToDouble(Double::doubleValue).average().getAsDouble();
        }
        return new Statistic(o.getId(), a, b);
    }
}
