package app.dtos;


import app.entities.Room;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDTO {


    private int id;
    private int hotelId;
    private String number;
    private double price;

    public RoomDTO(Room room) {
        this.id = room.getId();
        this.hotelId = room.getHotel().getId();
        this.number = room.getNumber();
        this.price = room.getPrice();
    }
}
