package app.dtos;


import app.entities.Hotel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelDTO {

    private int id;
    private String name;
    private String address;
    @Singular
    private List<RoomDTO> rooms;

}
