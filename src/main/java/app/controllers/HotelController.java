package app.controllers;

import app.dtos.HotelDTO;
import app.services.HotelService;
import io.javalin.http.Context;

public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    public void getAllHotels(Context ctx){
        ctx.json(hotelService.getAllHotels());
    }

    public void getHotelById(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        HotelDTO hotelDTO = hotelService.getHotelById(id);
        if(hotelDTO == null){
            ctx.status(404);
        } else {
            ctx.json(hotelDTO); //automatic sends status code 200 at this point!
        }
    }

    public void getRoomsForHotel(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        HotelDTO hotelDTO = hotelService.getHotelById(id);
        if(hotelDTO == null){
            ctx.status(404);
        } else {
            ctx.json(hotelDTO.getRooms());
        }
    }

    public void createHotel(Context ctx){
        HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);
        if(hotelDTO.getName() == null || hotelDTO.getAddress() == null){
            ctx.status(400);
            return;
        }
        HotelDTO created = hotelService.createHotel(hotelDTO);
        ctx.status(201).json(created);
    }

    public void updateHotel(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);
        HotelDTO updated = hotelService.updateHotel(id, hotelDTO);
        if(updated == null){
            ctx.status(404);
        } else {
            ctx.json(updated);
        }
    }

    public void deleteHotel(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        HotelDTO deleted = hotelService.getHotelById(id);
        if(deleted == null || !hotelService.deleteHotel(id)){
            ctx.status(404);
        } else {
            ctx.json(deleted);
        }
    }
}
