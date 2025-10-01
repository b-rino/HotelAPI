package app.routes;

import app.controllers.HotelController;
import app.daos.HotelDAO;
import app.daos.RoomDAO;
import app.services.HotelServiceImpl;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class HotelRoutes {


    private final HotelController hotelController;

    public HotelRoutes(EntityManagerFactory emf) {
        HotelDAO hotelDAO = new HotelDAO(emf);
        RoomDAO roomDAO = new RoomDAO(emf);
        HotelServiceImpl hotelService = new HotelServiceImpl(hotelDAO, roomDAO);
        this.hotelController = new HotelController(hotelService);
    }

    //method referencing for route registration because we are NOT using a handler in RoomController!
    //Godt valg til CRUD end-points (nemt at teste), men ikke så fleksibel som en Handler (koblet til en controller-instans)
    public EndpointGroup getRoutes() {
        return () -> {
            get("/", hotelController::getAllHotels);
            post("/", hotelController::createHotel);
            path("{id}", () -> {
                get(hotelController::getHotelById);
                put(hotelController::updateHotel);
                delete(hotelController::deleteHotel);
                get("rooms", hotelController::getRoomsForHotel);
            });
        };
    }
}
