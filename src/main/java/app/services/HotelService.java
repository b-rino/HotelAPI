package app.services;

import app.dtos.HotelDTO;
import app.dtos.RoomDTO;

import java.util.List;

public interface HotelService {

    List<HotelDTO> getAllHotels();

    HotelDTO getHotelById(int id);

    HotelDTO createHotel(HotelDTO hotelDTO);

    HotelDTO updateHotel(int id, HotelDTO hotelDTO);

    boolean deleteHotel(int id);

    RoomDTO addRoom(RoomDTO roomDTO);

    void removeRoom(int roomId);

    RoomDTO getRoomById(int id);

    List<RoomDTO> getRoomsForHotel(int hotelId);
}
