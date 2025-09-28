package app.controllers;

import app.dtos.HotelDTO;
import app.services.HotelService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    public void getAllHotels(Context ctx) {
        ctx.status(HttpStatus.OK).json(hotelService.getAllHotels());
    }

    public void getHotelById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        HotelDTO hotelDTO = hotelService.getHotelById(id);
        ctx.status(HttpStatus.OK).json(hotelDTO);
    }

    public void getRoomsForHotel(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        HotelDTO hotelDTO = hotelService.getHotelById(id);
        ctx.status(HttpStatus.OK).json(hotelDTO.getRooms());
    }

    public void createHotel(Context ctx) {
        HotelDTO hotelDTO = ctx.bodyValidator(HotelDTO.class)
                .check(dto -> dto.getName() != null && !dto.getName().isBlank(), "Hotel name is required")
                .check(dto -> dto.getAddress() != null && !dto.getAddress().isBlank(), "Hotel address is required")
                .get();

        HotelDTO created = hotelService.createHotel(hotelDTO);
        ctx.status(HttpStatus.CREATED).json(created);
    }

    public void updateHotel(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        HotelDTO hotelDTO = ctx.bodyValidator(HotelDTO.class)
                .check(dto -> dto.getName() != null && !dto.getName().isBlank(), "Hotel name is required")
                .check(dto -> dto.getAddress() != null && !dto.getAddress().isBlank(), "Hotel address is required")
                .get();

        HotelDTO updated = hotelService.updateHotel(id, hotelDTO);
        ctx.status(HttpStatus.OK).json(updated);
    }

    public void deleteHotel(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        HotelDTO deleted = hotelService.getHotelById(id);
        hotelService.deleteHotel(id);
        ctx.status(HttpStatus.OK).json(deleted);
    }
}
