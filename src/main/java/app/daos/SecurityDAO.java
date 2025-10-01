package app.daos;

import app.config.HibernateConfig;
import app.entities.Role;
import app.entities.User;
import app.exceptions.EntityAlreadyExistsException;
import app.exceptions.EntityNotFoundException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class SecurityDAO implements ISecurityDAO {

    EntityManagerFactory emf;

    public SecurityDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public User getVerifiedUser(String username, String password) throws ValidationException {
        try(EntityManager em = emf.createEntityManager()) {
            User user = em.find(User.class, username);
            if (user == null) {
                throw new ValidationException("Invalid username or password");
            } else {
                //TODO: sout skal fjernes igen !
                System.out.println("Successfully verified user");
                return user;
            }
        }
    }

    @Override
    public User createUser(String username, String password) throws EntityAlreadyExistsException {
        try(EntityManager em = emf.createEntityManager()) {
            User user = em.find(User.class, username);
            if (user == null) {
                em.getTransaction().begin();
                em.persist(new User(username, password));
                em.getTransaction().commit();
                return user;
            } else throw new EntityAlreadyExistsException("Username already exists");
        }
    }


    @Override
    public User addUserRole(String username, String rolename) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            User user = em.find(User.class, username);
            Role role = em.find(Role.class, rolename);
            if (user == null || role == null) {
                throw new EntityNotFoundException("User or role does not exist!");
            }
            em.getTransaction().begin();
            user.addRole(role);
            em.getTransaction().commit();
            return user;
        }
    }

    public Role createRole(String roleName) throws EntityAlreadyExistsException {
        try (EntityManager em = emf.createEntityManager()) {
            Role role = em.find(Role.class, roleName);
            if(role == null) {
                role = new Role(roleName);
                em.getTransaction().begin();
                em.persist(role);
                em.getTransaction().commit();
                return role;
            } else {
                throw new EntityAlreadyExistsException("Role already exists");
            }
        }
    }

    public static void main(String[] args) throws EntityNotFoundException {
        SecurityDAO dao = new SecurityDAO(HibernateConfig.getEntityManagerFactory());

        dao.createUser("admin", "admin");
        dao.createRole("User");
        dao.addUserRole("admin", "User");
    }
}
