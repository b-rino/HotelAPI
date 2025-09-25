package app.services;

import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.daos.HotelDAO;
import app.daos.RoomDAO;

import java.util.List;

public class HotelServiceImpl implements HotelService {

    private final HotelDAO hotelDAO;
    private final RoomDAO roomDAO;

    public HotelServiceImpl(HotelDAO hotelDAO, RoomDAO roomDAO) {
        this.hotelDAO = hotelDAO;
        this.roomDAO = roomDAO;
    }

    @Override
    public List<HotelDTO> getAllHotels() {
        return hotelDAO.getAllHotels();
    }

    @Override
    public HotelDTO getHotelById(int id) {
        return hotelDAO.getHotelById(id);
    }

    @Override
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        return hotelDAO.createHotel(hotelDTO);
    }

    @Override
    public HotelDTO updateHotel(int id, HotelDTO hotelDTO) {
        return hotelDAO.updateHotel(id, hotelDTO);
    }

    @Override
    public boolean deleteHotel(int id) {
        return hotelDAO.deleteHotel(id);
    }

    @Override
    public void addRoom(Hotel hotel, Room room) {
        room.setHotel(hotel);
        hotel.getRooms().add(room);
        roomDAO.createRoom(room);
    }

    @Override
    public void removeRoom(Hotel hotel, Room room) {
        hotel.getRooms().remove(room);
        roomDAO.deleteRoom(room.getId());
    }

    @Override
    public List<RoomDTO> getRoomsForHotel(Hotel hotel) {
        return roomDAO.getRoomsByHotelId(hotel.getId());
    }
}
