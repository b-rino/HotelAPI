package app.controllers;

import app.config.HibernateConfig;
import app.daos.RoomDAO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.exceptions.RoomNotFoundException;
import app.mappers.RoomMapper;
import app.services.HotelService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class RoomController {

    private final HotelService hotelService;

    public RoomController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    public void addRoom(Context ctx) {
        RoomDTO dto = ctx.bodyValidator(RoomDTO.class)
                .check(d -> d.getHotelId() > 0, "Hotel ID is required")
                .check(d -> d.getNumber() != null && !d.getNumber().isBlank(), "Room number is required")
                .check(d -> d.getPrice() > 0, "Room price must be greater than 0")
                .get();

        RoomDTO created = hotelService.addRoom(dto); // throws HotelNotFoundException if hotel doesn't exist
        ctx.status(HttpStatus.CREATED).json(created);
    }

    public void deleteRoom(Context ctx) {
        int roomId = Integer.parseInt(ctx.pathParam("id"));

        RoomDTO roomDTO = hotelService.getRoomById(roomId);

        if (roomDTO == null) throw new RoomNotFoundException(roomId);

        hotelService.removeRoom(roomId);

        ctx.status(HttpStatus.OK).json(roomDTO);
    }



}
