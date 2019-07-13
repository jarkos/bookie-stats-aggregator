package stats;

import lombok.extern.log4j.Log4j2;
import model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
public class StatsAggregator {

    private static final double POLISH_TAX_FACTOR = 0.88;
    private static final double RANGE = 0.05;
    private static final int SINGLE_AMOUNT_OF_MONEY_PER_BET = 100;
    private static final int MONEY_POT = 1000;
    private OddsRepository oddsRepository = new OddsRepository();
    private SportEventRepository sportEventsRepository = new SportEventRepository();
    private static ResourceBundle rb = ResourceBundle.getBundle("app");

    public void getRoiStats() {
        countRoiForOddsInRange(2.55, EventType.TENIS);
        countRoiForOddsInRange(2.45, EventType.TENIS);
        countRoiForOddsInRange(2.35, EventType.TENIS);
        countRoiForOddsInRange(2.25, EventType.TENIS);
        countRoiForOddsInRange(2.15, EventType.TENIS);
        countRoiForOddsInRange(2.05, EventType.TENIS);
        countRoiForOddsInRange(1.95, EventType.TENIS);
        countRoiForOddsInRange(1.85, EventType.TENIS);
        countRoiForOddsInRange(1.75, EventType.TENIS);
        countRoiForOddsInRange(1.65, EventType.TENIS);
        countRoiForOddsInRange(1.55, EventType.TENIS);
        countRoiForOddsInRange(1.45, EventType.TENIS);
        countRoiForOddsInRange(1.35, EventType.TENIS);
        countRoiForOddsInRange(1.30, EventType.TENIS);
        countRoiForOddsInRange(1.25, EventType.TENIS);
        countRoiForOddsInRange(1.15, EventType.TENIS);
        countRoiForOddsInRange(1.05, EventType.TENIS);

        countRoiForOddsInRange(2.95, EventType.FOOTBALL);
        countRoiForOddsInRange(2.85, EventType.FOOTBALL);
        countRoiForOddsInRange(2.75, EventType.FOOTBALL);
        countRoiForOddsInRange(2.65, EventType.FOOTBALL);
        countRoiForOddsInRange(2.55, EventType.FOOTBALL);
        countRoiForOddsInRange(2.45, EventType.FOOTBALL);
        countRoiForOddsInRange(2.35, EventType.FOOTBALL);
        countRoiForOddsInRange(2.25, EventType.FOOTBALL);
        countRoiForOddsInRange(2.15, EventType.FOOTBALL);
        countRoiForOddsInRange(2.05, EventType.FOOTBALL);
        countRoiForOddsInRange(1.95, EventType.FOOTBALL);
        countRoiForOddsInRange(1.85, EventType.FOOTBALL);
        countRoiForOddsInRange(1.75, EventType.FOOTBALL);
        countRoiForOddsInRange(1.65, EventType.FOOTBALL);
        countRoiForOddsInRange(1.55, EventType.FOOTBALL);
        countRoiForOddsInRange(1.45, EventType.FOOTBALL);
        countRoiForOddsInRange(1.35, EventType.FOOTBALL);
        countRoiForOddsInRange(1.30, EventType.FOOTBALL);
        countRoiForOddsInRange(1.25, EventType.FOOTBALL);
        countRoiForOddsInRange(1.15, EventType.FOOTBALL);
        countRoiForOddsInRange(1.05, EventType.FOOTBALL);
    }

    private void countRoiForOddsInRange(double oddsValue, EventType eventType) {
        List<Integer> idsOdsInRange = oddsRepository.getAllOddsIdsInRangeByType(oddsValue, eventType);
        List<Odds> odsWithValueInRange = oddsRepository.getAllOddsByIds(idsOdsInRange);
        var allStatisticsWithAverageOdds = odsWithValueInRange.stream().map(o -> mapToStatistic(o, oddsValue)).collect(Collectors.toList());
        List<Statistic> filteredStats = allStatisticsWithAverageOdds.stream().filter(s -> areOddsInRange(s, oddsValue)).collect(Collectors.toList());
        AtomicInteger successfulPredictionResultCount = new AtomicInteger();
        double statsSizeForRange = filteredStats.size();
        filteredStats.forEach(stat -> {
            SportEvent se = sportEventsRepository.getSportEventByOddsId(stat.getId());
            boolean check = checkResultCorrectlyPredicted(stat, se);
            if (check) {
                successfulPredictionResultCount.getAndIncrement();
            }
        });
        prepareRoiForDoubleBet(oddsValue, eventType, successfulPredictionResultCount, statsSizeForRange);
        prepareRoiResultForSingleBet(oddsValue, successfulPredictionResultCount, statsSizeForRange);
    }

    private void prepareRoiForDoubleBet(double avgOddValue, EventType eventType, AtomicInteger successfulPredictionResultsCount, double statsSizeForRange) {
        BigDecimal finalRoiDoubleBet;
        var winProbability = successfulPredictionResultsCount.doubleValue() / statsSizeForRange;
        var possibleWinAmountOfDoubleAkoBet = avgOddValue * avgOddValue * POLISH_TAX_FACTOR * SINGLE_AMOUNT_OF_MONEY_PER_BET;
        var doubledBetProbability = winProbability * winProbability;
        var winsRatioForTenDoubleAkoBets = BigDecimal.valueOf(doubledBetProbability).setScale(2, RoundingMode.DOWN).multiply(BigDecimal.valueOf((10)));
        var winValueForTenDoubleAkoBets = winsRatioForTenDoubleAkoBets.multiply(BigDecimal.valueOf(possibleWinAmountOfDoubleAkoBet));
        if (winValueForTenDoubleAkoBets.compareTo(BigDecimal.ZERO) >= 0) {
            finalRoiDoubleBet = winValueForTenDoubleAkoBets.subtract(BigDecimal.valueOf(MONEY_POT));
        } else {
            finalRoiDoubleBet = winValueForTenDoubleAkoBets.add(BigDecimal.valueOf(MONEY_POT));
        }

        log.info("Result for " + successfulPredictionResultsCount + "/" + statsSizeForRange + " odds " + eventType.name() + " "
                + avgOddValue + ": " + winProbability + " | Doubled: " + doubledBetProbability +
                " WinPerBet100PLN: " + possibleWinAmountOfDoubleAkoBet);

        if (finalRoiDoubleBet.doubleValue() < -200.0) {
            log.info("Double bet ROI: " + finalRoiDoubleBet);
        } else {
            log.warn("Double bet ROI: " + finalRoiDoubleBet);
        }
    }

    //TODO refactor with doubleRoi
    private void prepareRoiResultForSingleBet(double oddsValue, AtomicInteger successfulPredictionResultCount, double statsSizeForRange) {
        BigDecimal finalRoiSingleBet;
        var possibilityWin = successfulPredictionResultCount.doubleValue() / statsSizeForRange;
        var winningBetAmountOfSingleBet = oddsValue * POLISH_TAX_FACTOR * SINGLE_AMOUNT_OF_MONEY_PER_BET;
        var possiblyWinForOneBet = (BigDecimal.valueOf(possibilityWin).setScale(2, RoundingMode.DOWN).multiply(BigDecimal.valueOf((10))));
        var winValueForOneBet = possiblyWinForOneBet.multiply(BigDecimal.valueOf(winningBetAmountOfSingleBet));

        if (winValueForOneBet.compareTo(BigDecimal.ZERO) >= 0) {
            finalRoiSingleBet = winValueForOneBet.subtract(BigDecimal.valueOf(MONEY_POT));
        } else {
            finalRoiSingleBet = winValueForOneBet.add(BigDecimal.valueOf(MONEY_POT));
        }

        if (finalRoiSingleBet.doubleValue() < -200.0) {
            log.info("Single bet ROI: " + finalRoiSingleBet);
        } else {
            log.warn("Single bet ROI: " + finalRoiSingleBet);
        }

    }

    private boolean areOddsInRange(Statistic statistic, double oddsLessThanValue) {
        var loweRange = oddsLessThanValue - RANGE;
        if (statistic.getAverageOdds1() != 0.0) {
            return statistic.getAverageOdds1() >= loweRange;
        } else {
            return statistic.getAverageOdds2() >= loweRange;
        }
    }

    private boolean checkResultCorrectlyPredicted(Statistic stat, SportEvent se) {
        boolean check;
        if (stat.getAverageOdds1() >= 1.0) {
            check = se.getFirstTeamResult() > se.getSecondTeamResult();
        } else {
            check = se.getFirstTeamResult() < se.getSecondTeamResult();
        }
        return check;
    }

    private Statistic mapToStatistic(Odds o, double oddsValue) {
        HashMap<String, List<Double>> oddsMap = new HashMap<>();
        oddsMap.put("1", new ArrayList<>());
        oddsMap.put("2", new ArrayList<>());
        double range = Double.valueOf(rb.getString("aggregation.stats.odds.range"));
        var lowerRange = oddsValue - range;
        var higherRange = oddsValue + range;
        if ((o.getBookieA_1_odds() <= higherRange && o.getBookieA_1_odds() >= 0 && o.getBookieA_1_odds() >= lowerRange)
                || (o.getBookieB_1_odds() <= higherRange && o.getBookieB_1_odds() >= 0 && o.getBookieB_1_odds() >= lowerRange)
                || (o.getBookieC_1_odds() <= higherRange && o.getBookieC_1_odds() >= 0 && o.getBookieC_1_odds() >= lowerRange)
                || (o.getBookieD_1_odds() <= higherRange && o.getBookieD_1_odds() >= 0) && o.getBookieD_1_odds() >= lowerRange) {
            ArrayList<Double> list = new ArrayList<>();
            list.add(o.getBookieA_1_odds());
            list.add(o.getBookieB_1_odds());
            list.add(o.getBookieC_1_odds());
            list.add(o.getBookieD_1_odds());
            oddsMap.put("1", list);
        } else if ((o.getBookieA_2_odds() <= higherRange && o.getBookieA_2_odds() >= 0 && o.getBookieA_2_odds() >= lowerRange)
                || (o.getBookieB_2_odds() <= higherRange && o.getBookieB_2_odds() >= 0 && o.getBookieB_2_odds() >= lowerRange)
                || (o.getBookieC_2_odds() <= higherRange && o.getBookieC_2_odds() >= 0 && o.getBookieC_2_odds() >= lowerRange)
                || (o.getBookieD_2_odds() <= higherRange && o.getBookieD_2_odds() >= 0 && o.getBookieD_2_odds() >= lowerRange)) {
            ArrayList<Double> list = new ArrayList<>();
            list.add(o.getBookieA_2_odds());
            list.add(o.getBookieB_2_odds());
            list.add(o.getBookieC_2_odds());
            list.add(o.getBookieD_2_odds());
            oddsMap.put("2", list);
        }
        double averageOddsFor1Win = 0, averageOddsFor2Win = 0;
        if (!oddsMap.get("1").isEmpty()) {
            averageOddsFor1Win = oddsMap.get("1").stream().filter(x -> !x.equals(0.0)).mapToDouble(Double::doubleValue).average().getAsDouble();
        }
        if (!oddsMap.get("2").isEmpty()) {
            averageOddsFor2Win = oddsMap.get("2").stream().filter(x -> !x.equals(0.0)).mapToDouble(Double::doubleValue).average().getAsDouble();
        }
        return new Statistic(o.getId(), averageOddsFor1Win, averageOddsFor2Win);
    }
}
