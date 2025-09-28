package app.dtos;


import app.entities.Hotel;
import app.entities.Room;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDTO {


    private int id;
    private int hotelId;
    private String number;
    private double price;

}
