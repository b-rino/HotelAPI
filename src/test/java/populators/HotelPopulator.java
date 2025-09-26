package populators;


import app.config.HibernateConfig;
import app.daos.HotelDAO;
import app.daos.RoomDAO;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class HotelPopulator {

    public static List<HotelDTO> seededHotels = new ArrayList<>();

    public static void seedDatabase() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        HotelDAO hotelDAO = new HotelDAO(emf);
        RoomDAO roomDAO = new RoomDAO(emf);

        HotelDTO h1 = hotelDAO.create(HotelDTO.builder()
                .name("Backend Inn")
                .address("Layered Lane 42")
                .build());

        HotelDTO h2 = hotelDAO.create(HotelDTO.builder().name("DTO Palace").address("Abstraction Ave 7").build());

        RoomDTO room1 = RoomDTO.builder()
                .hotelId(h1.getId())
                .number("101")
                .price(1200.0)
                .build();

        RoomDTO room2 = RoomDTO.builder()
                .hotelId(h1.getId())
                .number("102")
                .price(1300.0)
                .build();

        RoomDTO room3 = RoomDTO.builder()
                .hotelId(h2.getId())
                .number("201")
                .price(1500.0)
                .build();

        roomDAO.create(room1);
        roomDAO.create(room2);
        roomDAO.create(room3);


        seededHotels = new ArrayList<>(List.of(h1, h2));
        System.out.println("Seeded " + seededHotels.size() + " hotels.");
    }

    public static void clearDatabase() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();
            em.getTransaction().commit();
        }
        seededHotels.clear();
    }
}
