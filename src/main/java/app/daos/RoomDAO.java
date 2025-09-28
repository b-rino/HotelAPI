package app.daos;

import app.entities.Hotel;
import app.entities.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class RoomDAO {

    private final EntityManagerFactory emf;

    public RoomDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Room create(Room room) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Hotel skal allerede være sat på room via service-laget
            em.persist(room);

            em.getTransaction().commit();
            return room;
        }
    }

    public Room getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Room.class, id);
        }
    }

    public boolean delete(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Room room = em.find(Room.class, id);
            if (room == null) {
                em.getTransaction().rollback();
                return false;
            }

            //breaking bi-directional link to be able to delete without any hibernate errors!
            Hotel hotel = room.getHotel();
            if (hotel != null) {
                hotel.getRooms().remove(room);
            }
            em.remove(room);
            em.getTransaction().commit();
            return true;
        }
    }

    public List<Room> getRoomsByHotelId(int hotelId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("FROM Room WHERE hotel.id = :hotelId", Room.class)
                    .setParameter("hotelId", hotelId)
                    .getResultList();
        }
    }
}
