package populators;

import app.config.HibernateConfig;
import app.daos.HotelDAO;
import app.daos.RoomDAO;
import app.daos.SecurityDAO;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.dtos.UserDTO;
import app.entities.Hotel;
import app.entities.Role;
import app.entities.Room;
import app.entities.User;
import app.exceptions.EntityNotFoundException;
import app.mappers.HotelMapper;
import app.mappers.RoomMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Populator {

    public static List<HotelDTO> seededHotels = new ArrayList<>();
    public static List<UserDTO> seededUsers = new ArrayList();

    public static void seedHotels() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        HotelDAO hotelDAO = new HotelDAO(emf);
        RoomDAO roomDAO = new RoomDAO(emf);


        Hotel h1Entity = hotelDAO.create(HotelMapper.toEntity(
                HotelDTO.builder().name("Backend Inn").address("Layered Lane 42").build()
        ));
        Hotel h2Entity = hotelDAO.create(HotelMapper.toEntity(
                HotelDTO.builder().name("DTO Palace").address("Abstraction Ave 7").build()
        ));


        Room r1Entity = roomDAO.create(RoomMapper.toEntity(
                RoomDTO.builder().hotelId(h1Entity.getId()).number("101").price(1200.0).build(),
                h1Entity
        ));
        Room r2Entity = roomDAO.create(RoomMapper.toEntity(
                RoomDTO.builder().hotelId(h1Entity.getId()).number("102").price(1300.0).build(),
                h1Entity
        ));
        Room r3Entity = roomDAO.create(RoomMapper.toEntity(
                RoomDTO.builder().hotelId(h2Entity.getId()).number("201").price(1500.0).build(),
                h2Entity
        ));


        HotelDTO h1 = HotelMapper.toDTO(h1Entity);
        HotelDTO h2 = HotelMapper.toDTO(h2Entity);

        seededHotels = new ArrayList<>(List.of(h1, h2));
        System.out.println("Seeded " + seededHotels.size() + " hotels.");
    }


    public static void seedRoles() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(new Role("User"));
            em.persist(new Role("Admin"));
            em.getTransaction().commit();
        }
        System.out.println("Seeded roles: User + Admin");
    }


    public static void seedUsers() throws EntityNotFoundException {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        SecurityDAO dao = new SecurityDAO(emf);
        seededUsers.clear();

        if (!dao.existingUsername("User")) {
            User user = dao.createUser("User", "1234");
            User userWithRole = dao.addUserRole(user.getUsername(), "User");

            Set<String> userRoles = userWithRole.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toSet());

            seededUsers.add(new UserDTO(userWithRole.getUsername(), userRoles));
        }

        if (!dao.existingUsername("Admin")) {
            User admin = dao.createUser("Admin", "1234");
            User adminWithRole = dao.addUserRole(admin.getUsername(), "Admin");

            Set<String> adminRoles = adminWithRole.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toSet());

            seededUsers.add(new UserDTO(adminWithRole.getUsername(), adminRoles));
        }

        System.out.println("Seeded users: " + seededUsers);
    }


    public static void clearDatabase() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        }
        seededHotels.clear();
        seededUsers.clear();
    }
}
