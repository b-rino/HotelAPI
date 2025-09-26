package app.daos;

import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class RoomDAO {

    private final EntityManagerFactory emf;
    public RoomDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }


    public RoomDTO create(RoomDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Hotel hotel = em.find(Hotel.class, dto.getHotelId());
            if (hotel == null) {
                em.getTransaction().rollback();
                return null;
            }

            Room room = new Room(dto);
            room.setHotel(hotel);

            em.persist(room);
            em.getTransaction().commit();

            // Return fresh DTO with generated ID and hotel link
            return new RoomDTO(room);
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
            em.remove(room);
            em.getTransaction().commit();
            return true;
        }
    }


    public List<RoomDTO> getRoomsByHotelId(int hotelId) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Room> rooms = em.createQuery("FROM Room WHERE hotel.id = :hotelId", Room.class)
                    .setParameter("hotelId", hotelId)
                    .getResultList();
            return rooms.stream().map(RoomDTO::new).collect(Collectors.toList());
        }
    }
}
