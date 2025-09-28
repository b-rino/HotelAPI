package app.mappers;

import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;

public class RoomMapper {

    public static RoomDTO toDTO(Room room) {
        if (room == null) return null;

        return RoomDTO.builder()
                .id(room.getId())
                .hotelId(room.getHotel() != null ? room.getHotel().getId() : 0)
                .number(room.getNumber())
                .price(room.getPrice())
                .build();
    }

    public static Room toEntity(RoomDTO dto, Hotel hotel) {
        if (dto == null) return null;

        Room room = new Room();
        room.setNumber(dto.getNumber());
        room.setPrice(dto.getPrice());
        room.setHotel(hotel);
        return room;
    }

    //Uden bi-directional link ... god til at slette uden at støde på "detached"
    public static Room toEntity(RoomDTO dto) {
        if (dto == null) return null;

        Room room = new Room();
        room.setId(dto.getId());
        room.setNumber(dto.getNumber());
        room.setPrice(dto.getPrice());
        return room;
    }
}
