package model;

import org.hibernate.query.Query;

import java.util.List;
import java.util.ResourceBundle;

public class OddsRepository extends AbstractRepository {

    private static ResourceBundle rb = ResourceBundle.getBundle("app");

    public List getAllOdds() {
        String hql = "FROM Odds AS o";
        Query query = session.createQuery(hql);
        return query.list();
    }

    public List<Odds> getAllOddsByIds(List<Integer> ids) {
        String hql = "FROM Odds AS o WHERE o.id in :ids";
        Query query = session.createQuery(hql, Odds.class).setParameter("ids", ids);
        return query.list();
    }

    public List<Integer> getAllOddsIdsInRangeByType(double oddsValue, EventType type) {
        double range = Double.valueOf(rb.getString("aggregation.stats.odds.range"));
        var lowerRange = oddsValue - range;
        var higherRange = oddsValue + range;

        String hql = "SELECT o.id " +
                "FROM Odds AS o JOIN SportEvent AS se on se.odds.id = o.id " +
                "WHERE se.type = :type " +
                "AND ((o.bookieA_1_odds <= :higherRange AND o.bookieA_1_odds > 0 AND o.bookieA_1_odds >= :lowerRange) " +
                "OR (o.bookieA_2_odds <= :higherRange AND o.bookieA_2_odds > 0 AND o.bookieA_2_odds >= :lowerRange) " +
                "OR (o.bookieB_1_odds <= :higherRange AND o.bookieB_1_odds > 0 AND o.bookieA_1_odds >= :lowerRange) " +
                "OR (o.bookieB_2_odds <= :higherRange AND o.bookieB_2_odds > 0 AND o.bookieA_2_odds >= :lowerRange) " +
                "OR (o.bookieC_1_odds <= :higherRange AND o.bookieC_1_odds > 0 AND o.bookieA_1_odds >= :lowerRange) " +
                "OR (o.bookieC_2_odds <= :higherRange AND o.bookieC_2_odds > 0 AND o.bookieA_2_odds >= :lowerRange) " +
                "OR (o.bookieD_1_odds <= :higherRange AND o.bookieD_1_odds > 0 AND o.bookieA_1_odds >= :lowerRange) " +
                "OR (o.bookieD_2_odds <= :higherRange AND o.bookieD_2_odds > 0 AND o.bookieA_2_odds >= :lowerRange))";

        Query query = session.createQuery(hql, Integer.class).setParameter("lowerRange", lowerRange)
                .setParameter("higherRange", higherRange).setParameter("type", type);
        return query.list();
    }

}
