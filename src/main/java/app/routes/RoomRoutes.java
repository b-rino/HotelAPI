package app.routes;

import app.controllers.RoomController;
import app.daos.HotelDAO;
import app.daos.RoomDAO;
import app.services.HotelServiceImpl;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.post;

public class RoomRoutes {

    private final RoomController roomController;

    public RoomRoutes(EntityManagerFactory emf) {
        HotelDAO hotelDAO = new HotelDAO(emf);
        RoomDAO roomDAO = new RoomDAO(emf);
        HotelServiceImpl hotelService = new HotelServiceImpl(hotelDAO, roomDAO);
        this.roomController = new RoomController(hotelService);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            post("/", roomController::addRoom);
            delete("{id}", roomController::deleteRoom);
        };
    }
}
