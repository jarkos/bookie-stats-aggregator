package model;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.PersistenceException;

@Log4j2
public class SportEventRepository {

    private Session session;

    public SportEventRepository() {
        createSession();
    }

    private void createSession() {
        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml");
        session = config.buildSessionFactory().openSession();
    }

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

    //        // Query - To fetch all employees.
//        List<SportEvent> list = s.getNamedQuery("findSportEvents").getResultList();

}