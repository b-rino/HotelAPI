package app.routes;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {


    private final HotelRoutes hotelRoutes;

    private final RoomRoutes roomRoutes;

    private final SecurityRoutes securityRoutes;


    public Routes(EntityManagerFactory emf) {
        this.hotelRoutes = new HotelRoutes(emf);
        this.roomRoutes = new RoomRoutes(emf);
        this.securityRoutes = new SecurityRoutes();
    }

    public EndpointGroup getRoutes(){
        return () -> {
            get("/", ctx -> ctx.result("Hello World!"));
            path("hotel", hotelRoutes.getRoutes());
            path("room", roomRoutes.getRoutes());
            path("auth", securityRoutes.getRoutes());
        };
    }
}
