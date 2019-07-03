package model;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Transaction;

import javax.persistence.PersistenceException;

@Log4j2
public class SportEventRepository extends AbstractRepository {

    public void saveSportEvents(SportEvent se) {
        Transaction t = session.beginTransaction();
        try {
            session.persist(se);
        } catch (PersistenceException pe) {
            //do nothing, it's duplicated
            log.warn("Duplicate: " + se.toString());
        } catch (Exception e) {
            log.error(e);
        }
        t.commit();
    }

    public boolean checkEventExist(String id) {
        var query = session.createQuery("from SportEvent WHERE flashScoreEventId = :id").setParameter("id", id);
        return query.list().size() > 0;
    }

    public SportEvent getSportEventByOddsId(int oddsId) {
        var query = session.createQuery("from SportEvent WHERE odds_id = :oddsId").setParameter("oddsId", oddsId);
        return (SportEvent) query.uniqueResult();
    }


}
