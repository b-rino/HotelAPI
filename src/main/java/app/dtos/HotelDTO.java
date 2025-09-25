package app.dtos;


import app.entities.Hotel;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDTO {

    private int id;
    private String name;
    private String address;
    @Singular
    private List<RoomDTO> rooms;

    public HotelDTO(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.rooms = hotel.getRooms().stream().map(RoomDTO::new).toList();
    }

    public Hotel toEntity() {
        return new Hotel(this);
    }
}
