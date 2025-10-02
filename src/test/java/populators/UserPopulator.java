package populators;

import app.config.HibernateConfig;
import app.daos.SecurityDAO;
import app.entities.User;
import app.exceptions.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.ArrayList;
import java.util.List;

public class UserPopulator {

    public static List<User> seededUsers = new ArrayList<>();

    public static void seedUsers() {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        SecurityDAO dao = new SecurityDAO(emf);

        try {
            User u1 = dao.createUser("Benjamin", "1234");

            User u1WithRole = dao.addUserRole("Benjamin", "User");

            User u2 = dao.createUser("Pedro", "password");

            User u2WithRole = dao.addUserRole("Pedro", "User");

            seededUsers = new ArrayList<>(List.of(u1WithRole, u2WithRole));
            System.out.println("Seeded database with " + seededUsers.size() + " users");
        } catch (EntityNotFoundException e) {
            System.out.println("Failed to seed database");
        }
    }

    public static void clearDatabase() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        }
        seededUsers.clear();
    }
}
