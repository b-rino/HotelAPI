package app.services;

import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.daos.HotelDAO;
import app.daos.RoomDAO;
import app.exceptions.HotelNotFoundException;
import app.exceptions.RoomNotFoundException;
import app.mappers.HotelMapper;
import app.mappers.RoomMapper;

import java.util.List;
import java.util.stream.Collectors;

public class HotelServiceImpl implements HotelService {

    private final HotelDAO hotelDAO;
    private final RoomDAO roomDAO;

    public HotelServiceImpl(HotelDAO hotelDAO, RoomDAO roomDAO) {
        this.hotelDAO = hotelDAO;
        this.roomDAO = roomDAO;
    }

    @Override
    public List<HotelDTO> getAllHotels() {
        return hotelDAO.getAll().stream()
                .map(HotelMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HotelDTO getHotelById(int id) {
        Hotel hotel = hotelDAO.getById(id);
        if (hotel == null) throw new HotelNotFoundException(id);
        return HotelMapper.toDTO(hotel);
    }

    public RoomDTO getRoomById(int id) {
        Room room = roomDAO.getById(id);
        if (room == null) throw new RoomNotFoundException(id);
        return RoomMapper.toDTO(room);
    }

    @Override
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        Hotel hotel = HotelMapper.toEntity(hotelDTO);
        Hotel saved = hotelDAO.create(hotel);
        return HotelMapper.toDTO(saved);
    }

    @Override
    public HotelDTO updateHotel(int id, HotelDTO hotelDTO) {
        Hotel existing = hotelDAO.getById(id);
        if (existing == null) throw new HotelNotFoundException(id);

        existing.setName(hotelDTO.getName());
        existing.setAddress(hotelDTO.getAddress());

        Hotel updated = hotelDAO.update(id, existing);
        return HotelMapper.toDTO(updated);
    }

    @Override
    public boolean deleteHotel(int id) {
        Hotel hotel = hotelDAO.getById(id);
        if (hotel == null) throw new HotelNotFoundException(id);
        return hotelDAO.delete(id);
    }

    @Override
    public RoomDTO addRoom(RoomDTO roomDTO) {
        Hotel hotel = hotelDAO.getById(roomDTO.getHotelId());
        if (hotel == null) throw new HotelNotFoundException(roomDTO.getHotelId());

        Room room = RoomMapper.toEntity(roomDTO, hotel);
        Room saved = roomDAO.create(room);
        return RoomMapper.toDTO(saved);
    }

    @Override
    public void removeRoom(int roomId) {
        Room room = roomDAO.getById(roomId);
        if (room == null) throw new RoomNotFoundException(roomId);

        boolean deleted = roomDAO.delete(roomId);
        if (!deleted) throw new RoomNotFoundException(roomId);
    }


    @Override
    public List<RoomDTO> getRoomsForHotel(int hotelId) {
        Hotel hotel = hotelDAO.getById(hotelId);
        if (hotel == null) throw new HotelNotFoundException(hotelId);

        List<Room> rooms = roomDAO.getRoomsByHotelId(hotelId);
        return rooms.stream()
                .map(RoomMapper::toDTO)
                .collect(Collectors.toList());
    }
}
