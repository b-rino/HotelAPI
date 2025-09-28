package app.mappers;

import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HotelMapper {

    public static HotelDTO toDTO(Hotel hotel) {
        HotelDTO dto = new HotelDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setAddress(hotel.getAddress());

        dto.setRooms(
                hotel.getRooms() != null
                        ? hotel.getRooms().stream()
                        .map(RoomMapper::toDTO)
                        .collect(Collectors.toList())
                        : new ArrayList<>()
        );

        return dto;
    }



    public static Hotel toEntity(HotelDTO dto) {
        if (dto == null) return null;

        Hotel hotel = new Hotel();
        hotel.setName(dto.getName());
        hotel.setAddress(dto.getAddress());

        if (dto.getRooms() != null) {
            List<Room> rooms = dto.getRooms().stream()
                    .map(roomDTO -> RoomMapper.toEntity(roomDTO, hotel))
                    .collect(Collectors.toList());
            hotel.setRooms(rooms);
        }

        return hotel;
    }

}
