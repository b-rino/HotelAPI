package app.dtos;


import app.entities.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {

    private int id;
    private String name;
    private String address;
    private List<RoomDTO> rooms;

    public HotelDTO(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.rooms = hotel.getRooms().stream().map(RoomDTO::new).toList();
    }
}
