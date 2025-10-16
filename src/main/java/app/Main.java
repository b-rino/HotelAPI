package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;


public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        ApplicationConfig.startServer(7073, emf);
    }
}
