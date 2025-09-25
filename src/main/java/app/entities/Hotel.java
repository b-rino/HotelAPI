package app.entities;


import app.dtos.HotelDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 100)
    private String name;
    @Column(length = 100)
    private String address;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    public Hotel(HotelDTO hotelDTO) {
        this.name = hotelDTO.getName();
        this.address = hotelDTO.getAddress();

        if(hotelDTO.getRooms() != null) {
            this.rooms = hotelDTO.getRooms().stream().map(roomDTO -> {
                Room room = new Room(roomDTO);
                room.setHotel(this);
                return room;
            }).toList();
        } else {
            this.rooms = new ArrayList<>();
        }
    }


}
