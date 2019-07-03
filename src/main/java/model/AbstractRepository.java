package model;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

abstract class AbstractRepository {

    Session session;

    AbstractRepository() {
        createSession();
    }

    private void createSession() {
        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml");
        session = config.buildSessionFactory().openSession(); //TODO session close?
    }

}
