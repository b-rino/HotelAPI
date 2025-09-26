package app.controllers;

import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.services.HotelService;
import io.javalin.http.Context;

public class RoomController {


        private final HotelService hotelService;

    public RoomController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    public void addRoom(Context ctx) {
        RoomDTO dto = ctx.bodyAsClass(RoomDTO.class);
        if (dto.getHotelId() == 0 || dto.getNumber() == null || dto.getNumber().isBlank() || dto.getPrice() == 0) {
            ctx.status(400).result("Missing required fields");
            return;
        }

        RoomDTO created = hotelService.addRoom(dto);
        if (created == null) {
            ctx.status(404).result("Hotel not found");
        } else {
            ctx.status(201).json(created);
        }
    }


        public void deleteRoom(Context ctx) {
        int roomId = Integer.parseInt(ctx.pathParam("id"));
        RoomDTO dto = hotelService.getRoomsForHotel(null).stream()
                .filter(r -> r.getId() == roomId)
                .findFirst()
                .orElse(null);

        if (dto == null) {
            ctx.status(404).result("Room not found");
            return;
        }

        Hotel hotel = hotelService.getHotelById(dto.getHotelId()).toEntity();
        Room room = new Room(dto);
        hotelService.removeRoom(hotel, room);
        ctx.status(200).json(dto);

    }
    }
