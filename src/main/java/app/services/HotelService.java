package app.services;

import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;

import java.util.List;

public interface HotelService {
    List<HotelDTO> getAllHotels();
    HotelDTO getHotelById(int id);
    HotelDTO createHotel(HotelDTO hotelDTO);
    HotelDTO updateHotel(int id, HotelDTO hotelDTO);
    boolean deleteHotel(int id);
    void addRoom(Hotel hotel, Room room);
    void removeRoom(Hotel hotel, Room room);
    List<RoomDTO> getRoomsForHotel(Hotel hotel);
}
