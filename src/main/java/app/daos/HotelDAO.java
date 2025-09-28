package app.daos;

import app.entities.Hotel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class HotelDAO implements DAO<Hotel, Integer> {

    private final EntityManagerFactory emf;

    public HotelDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Hotel create(Hotel hotel) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(hotel);
            em.getTransaction().commit();
            return hotel;
        }
    }

    @Override
    public Hotel getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Hotel.class, id);
        }
    }

    @Override
    public Hotel update(Integer id, Hotel updatedData) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Hotel existing = em.find(Hotel.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setName(updatedData.getName());
            existing.setAddress(updatedData.getAddress());
            Hotel merged = em.merge(existing);
            em.getTransaction().commit();
            return merged;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Hotel hotel = em.find(Hotel.class, id);
            if (hotel == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(hotel);
            em.getTransaction().commit();
            return true;
        }
    }

    @Override
    public List<Hotel> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("FROM Hotel", Hotel.class).getResultList();
        }
    }
}
