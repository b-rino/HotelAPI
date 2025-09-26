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
        return hotelDAO.getAll();
    }

    @Override
    public HotelDTO getHotelById(int id) {
        return hotelDAO.getById(id);
    }

    @Override
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        return hotelDAO.create(hotelDTO);
    }

    @Override
    public HotelDTO updateHotel(int id, HotelDTO hotelDTO) {
        return hotelDAO.update(id, hotelDTO);
    }

    @Override
    public boolean deleteHotel(int id) {
        return hotelDAO.delete(id);
    }

    @Override
    public void addRoom(Hotel hotel, Room room) {
        room.setHotel(hotel);
        hotel.getRooms().add(room);
        RoomDTO  roomDTO = new RoomDTO(room);
        roomDAO.create(roomDTO);
    }

    @Override
    public void removeRoom(Hotel hotel, Room room) {
        hotel.getRooms().remove(room);
        roomDAO.delete(room.getId());
    }

    @Override
    public List<RoomDTO> getRoomsForHotel(Hotel hotel) {
        return roomDAO.getRoomsByHotelId(hotel.getId());
    }
}
