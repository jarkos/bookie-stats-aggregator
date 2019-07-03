package model;

import org.hibernate.query.Query;

import java.util.List;

public class OddsRepository extends AbstractRepository {

    public List getAllOdds() {
        String hql = "FROM Odds AS o";
        Query query = session.createQuery(hql);
        return query.list();
    }

    public List<Odds> getAllOddsLessThanValue(double lessThanOddsValue) {
        String hql = "FROM Odds AS o WHERE (o.bookieA_1_odds < :oddsValue AND o.bookieA_1_odds > 0) " +
                "OR (o.bookieA_2_odds < :oddsValue AND o.bookieA_2_odds > 0) " +
                "OR (o.bookieB_1_odds < :oddsValue AND o.bookieB_1_odds > 0) " +
                "OR (o.bookieB_2_odds < :oddsValue AND o.bookieB_2_odds > 0) " +
                "OR (o.bookieC_1_odds < :oddsValue AND o.bookieC_1_odds > 0) " +
                "OR (o.bookieC_2_odds < :oddsValue AND o.bookieC_2_odds > 0) " +
                "OR (o.bookieD_1_odds < :oddsValue AND o.bookieD_1_odds > 0) " +
                "OR (o.bookieD_2_odds < :oddsValue AND o.bookieD_2_odds > 0)";

        Query query = session.createQuery(hql, Odds.class).setParameter("oddsValue", lessThanOddsValue);
        return query.list();
    }

}
