package app.daos;

import app.config.HibernateConfig;
import app.dtos.HotelDTO;
import app.entities.Hotel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.stream.Collectors;


public class HotelDAO implements DAO<HotelDTO, Integer> {

    EntityManagerFactory emf;

    public HotelDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public HotelDTO create(HotelDTO hotelDTO) {
        Hotel hotel = hotelDTO.toEntity();
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(hotel);
            em.getTransaction().commit();
            return new HotelDTO(hotel);
        }
    }

    @Override
    public HotelDTO getById(Integer id) {
        try(EntityManager em = emf.createEntityManager()) {
            Hotel hotel = em.find(Hotel.class, id);
            return hotel != null ? new HotelDTO(hotel) : null;
        }
    }

    @Override
    public HotelDTO update(Integer integer, HotelDTO hotelDTO) {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Hotel hotel = em.find(Hotel.class, integer);
            if(hotel == null) {
                return null;
            }
            hotel.setName(hotelDTO.getName());
            hotel.setAddress(hotelDTO.getAddress());
            Hotel updatedHotel = em.merge(hotel);
            em.getTransaction().commit();
            return new HotelDTO(updatedHotel);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Hotel hotel = em.find(Hotel.class, id);
            if(hotel == null) {
                return false;
            }
            em.remove(hotel);
            em.getTransaction().commit();
            return true;
        }
    }

    @Override
    public List<HotelDTO> getAll() {
        try(EntityManager em = emf.createEntityManager()) {
            List<Hotel> hotels = em.createQuery("FROM Hotel", Hotel.class).getResultList();
            return hotels.stream().map(HotelDTO::new).collect(Collectors.toList());
        }
    }
}
