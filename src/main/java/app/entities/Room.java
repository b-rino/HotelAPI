package app.entities;


import app.dtos.RoomDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String number;

    private double price;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

}
