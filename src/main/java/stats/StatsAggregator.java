package stats;

import lombok.extern.log4j.Log4j2;
import model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
public class StatsAggregator {

    //TODO configurable by config
    private static final double POLISH_TAX_FACTOR = 0.88;
    private static final double RANGE = 0.05;
    private static final int SINGLE_AMOUNT_OF_MONEY_PER_BET = 100;
    private static final int MONEY_POT = 1000;
    private static final double WARN_MARGIN = -120.0;
    private OddsRepository oddsRepository = new OddsRepository();
    private SportEventRepository sportEventsRepository = new SportEventRepository();
    private static ResourceBundle rb = ResourceBundle.getBundle("app");

    public void getRoiStats() {
        //TODO use for/streams
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

        countRoiForOddsInRange(2.35, EventType.BASKETBALL);
        countRoiForOddsInRange(2.25, EventType.BASKETBALL);
        countRoiForOddsInRange(2.15, EventType.BASKETBALL);
        countRoiForOddsInRange(2.05, EventType.BASKETBALL);
        countRoiForOddsInRange(1.95, EventType.BASKETBALL);
        countRoiForOddsInRange(1.85, EventType.BASKETBALL);
        countRoiForOddsInRange(1.75, EventType.BASKETBALL);
        countRoiForOddsInRange(1.65, EventType.BASKETBALL);
        countRoiForOddsInRange(1.55, EventType.BASKETBALL);
        countRoiForOddsInRange(1.45, EventType.BASKETBALL);
        countRoiForOddsInRange(1.35, EventType.BASKETBALL);
        countRoiForOddsInRange(1.30, EventType.BASKETBALL);
        countRoiForOddsInRange(1.25, EventType.BASKETBALL);
        countRoiForOddsInRange(1.15, EventType.BASKETBALL);
        countRoiForOddsInRange(1.05, EventType.BASKETBALL);
    }

    private void countRoiForOddsInRange(double oddsValue, EventType eventType) {
        List<Integer> idsOdsInRange = oddsRepository.getAllOddsIdsInRangeByType(oddsValue, eventType);
        List<Odds> odsWithValueInRange = oddsRepository.getAllOddsByIds(idsOdsInRange);
        var allStatisticsWithAverageOdds = odsWithValueInRange.stream().map(o -> mapToStatistic(o, oddsValue)).collect(Collectors.toList());
        List<Statistic> filteredStats = allStatisticsWithAverageOdds.stream().filter(s -> areOddsInRange(s, oddsValue)).collect(Collectors.toList());
        final AtomicInteger successfulPredictionResultCount = new AtomicInteger();
        int statsSizeForRange = filteredStats.size();
        HashMap<String, Double> favoriteWinPerGameTypeMap = new HashMap<>();
        HashMap<String, Double> favoriteLosePerGameTypeMap = new HashMap<>();
        filteredStats.forEach(stat -> {
            SportEvent se = sportEventsRepository.getSportEventByOddsId(stat.getId());
            boolean check = checkResultCorrectlyPredicted(stat, se);
            if (check) {
                if (eventType.equals(EventType.FOOTBALL) && ((stat.getAverageOdds1() >= 1.90 && stat.getAverageOdds1() <= 2.40)
                        || (stat.getAverageOdds2() > 1.90 && stat.getAverageOdds2() <= 2.40))) {
                    incrementGameTypeCounterMapForEvent(favoriteWinPerGameTypeMap, se);
                }
                successfulPredictionResultCount.incrementAndGet();
            } else {
                if (eventType.equals(EventType.FOOTBALL) && ((stat.getAverageOdds1() >= 1.90 && stat.getAverageOdds1() <= 2.40)
                        || (stat.getAverageOdds2() > 1.90 && stat.getAverageOdds2() <= 2.40))) {
                    incrementGameTypeCounterMapForEvent(favoriteLosePerGameTypeMap, se);
                }
            }
        });
        log.error("Result for " + eventType.name() + " " + oddsValue + ": " + successfulPredictionResultCount + "/" + statsSizeForRange + " odds ");
        prepareRoiForAkoBet(oddsValue, successfulPredictionResultCount, statsSizeForRange, 5, "Five");
        prepareRoiForAkoBet(oddsValue, successfulPredictionResultCount, statsSizeForRange, 4, "Fourfold");
        prepareRoiForAkoBet(oddsValue, successfulPredictionResultCount, statsSizeForRange, 3, "Triple");
        prepareRoiForAkoBet(oddsValue, successfulPredictionResultCount, statsSizeForRange, 2, "Double");
        prepareRoiForAkoBet(oddsValue, successfulPredictionResultCount, statsSizeForRange, 1, "Single");
        printLeaguesRoiForHighBetRatioFavoriteWins(oddsValue, favoriteWinPerGameTypeMap, favoriteLosePerGameTypeMap);
    }

    private void printLeaguesRoiForHighBetRatioFavoriteWins(double oddsValue, HashMap<String, Double> favoriteWinPerGameTypeMap, HashMap<String, Double> favoriteLosePerGameTypeMap) {
        Map<String, Double> sortedFavoriteWinPerGameTypeMap = new LinkedHashMap<>();
        favoriteWinPerGameTypeMap.entrySet().stream().sorted(
                Map.Entry.comparingByValue()).forEachOrdered(
                c -> sortedFavoriteWinPerGameTypeMap.put(c.getKey(), c.getValue()));
        if (sortedFavoriteWinPerGameTypeMap.keySet().size() != 0) {
            sortedFavoriteWinPerGameTypeMap.forEach((k, v) -> {
                if (favoriteLosePerGameTypeMap.get(k) != null && favoriteLosePerGameTypeMap.get(k) > 2) {
                    var gameStatSize = favoriteLosePerGameTypeMap.get(k) + v;
                    log.info(k + " ## " + v + " %% " + (v / gameStatSize) * 100);
                    prepareRoiForAkoBet(oddsValue, new AtomicInteger(v.intValue()), gameStatSize, 1, "Single " + k);
                    prepareRoiForAkoBet(oddsValue, new AtomicInteger(v.intValue()), gameStatSize, 2, "Double " + k);
                    prepareRoiForAkoBet(oddsValue, new AtomicInteger(v.intValue()), gameStatSize, 3, "Triple " + k);
                }
            });
        }
    }

    private void incrementGameTypeCounterMapForEvent(HashMap<String, Double> perGameTypeMap, SportEvent se) {
        Double count = perGameTypeMap.get(se.getLeague());
        if (count == null) {
            count = 1.0;
        } else {
            count++;
        }
        perGameTypeMap.put(se.getLeague().split("-")[0], count);
    }

    private void prepareRoiForAkoBet(double avgOddValue, AtomicInteger successfulPredictionResultsCount, double statsSizeForRange, int numberOfBetsPerAko, String ID) {
        BigDecimal finalRoiForMultipleBet;
        var winProbability = successfulPredictionResultsCount.doubleValue() / statsSizeForRange;
        var possibleWinAmountOfAkoBet = Math.pow(avgOddValue, numberOfBetsPerAko) * POLISH_TAX_FACTOR * SINGLE_AMOUNT_OF_MONEY_PER_BET;
        var akoBetProbability = Math.pow(winProbability, numberOfBetsPerAko);
        var winsRatioForTenAkoBets = BigDecimal.valueOf(akoBetProbability).setScale(2, RoundingMode.DOWN).multiply(BigDecimal.valueOf((10)));
        var winValueForTenAkoBets = winsRatioForTenAkoBets.multiply(BigDecimal.valueOf(possibleWinAmountOfAkoBet));
        if (winValueForTenAkoBets.compareTo(BigDecimal.ZERO) >= 0) {
            finalRoiForMultipleBet = winValueForTenAkoBets.subtract(BigDecimal.valueOf(MONEY_POT));
        } else {
            finalRoiForMultipleBet = winValueForTenAkoBets.add(BigDecimal.valueOf(MONEY_POT));
        }
        DecimalFormat formatter = new DecimalFormat("#0.00");
        if (finalRoiForMultipleBet.doubleValue() < WARN_MARGIN) {
            log.info(ID + " " + formatter.format(akoBetProbability) + " ROI: " + formatter.format(finalRoiForMultipleBet)); //rounding missing in print
        } else {
            log.warn(ID + " " + formatter.format(akoBetProbability) + " ROI: " + formatter.format(finalRoiForMultipleBet)); //rounding missing in print
        }
    }

    private boolean areOddsInRange(Statistic statistic, double oddsLessThanValue) {
        var loweRange = oddsLessThanValue - RANGE;
        var higherRange = oddsLessThanValue + RANGE;
        if (statistic.getAverageOdds1() != 0.0) {
            return statistic.getAverageOdds1() >= loweRange && statistic.getAverageOdds1() <= higherRange;
        } else {
            return statistic.getAverageOdds2() >= loweRange && statistic.getAverageOdds2() <= higherRange;
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

    //TODO exclude
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
