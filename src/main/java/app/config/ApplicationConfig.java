package app.config;

import app.exceptions.*;
import app.routes.Routes;
import io.javalin.Javalin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);



    public static Javalin startServer(int port, EntityManagerFactory emf) {
        Routes routes = new Routes(emf);
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.bundledPlugins.enableRouteOverview("/routes");
            config.router.contextPath = "/api/v1";
            config.router.apiBuilder(routes.getRoutes());
        });

        configureLogging(app);
        configureExceptionHandling(app);

        app.start(port);
        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }

    private static void configureExceptionHandling(Javalin app) {
        app.exception(IllegalStateException.class, (e, ctx) -> {
            logger.warn("Bad request at [{}] {}: {}", ctx.method(), ctx.path(), e.getMessage());
            ctx.status(400).result("Invalid hotel or room data: " + e.getMessage());
        });

        app.exception(HotelNotFoundException.class, (e, ctx) -> {
            String message = "Hotel not found: " + e.getMessage();
            logger.warn("Handled HotelNotFoundException at [{}] {}: {}", ctx.method(), ctx.path(), message);
            ctx.status(404).result(message);
        });

        app.exception(RoomNotFoundException.class, (e, ctx) -> {
            String message = "Room not found: " + e.getMessage();
            logger.warn("Handled RoomNotFoundException at [{}] {}: {}", ctx.method(), ctx.path(), message);
            ctx.status(404).result(message);
        });

        app.exception(ValidationException.class, (e, ctx) -> {
            String message = "User not verified: " + e.getMessage();
            logger.warn("Handled ValidationException at [{}] {}: {}", ctx.method(), ctx.path(), message);
            ctx.status(400).result(message);
        });

        app.exception(EntityNotFoundException.class, (e, ctx) -> {
            String message = "Entity not found: " + e.getMessage();
            logger.warn("Handled EntityNotFoundException at [{}] {}: {}", ctx.method(), ctx.path(), message);
            ctx.status(404).result(message);
        });

        app.exception(EntityAlreadyExistsException.class, (e, ctx) -> {
            String message = "Entity already exists: " + e.getMessage();
            logger.error("Handled EntityAlreadyExistsException at [{}] {}: {}", ctx.method(), ctx.path(), message);
            ctx.status(409).result(message); // "409 Conflict"
        });



        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Unhandled exception at [{}] {}: {}", ctx.method(), ctx.path(), e.getMessage(), e);
            ctx.status(500).result("Internal server error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        });

    }


    private static void configureLogging(Javalin app) {
        app.before(ctx -> {
            logger.info("Incoming request: [{}] {} at {}", ctx.method(), ctx.path(), java.time.LocalDateTime.now());
            if (!ctx.body().isEmpty()) {
                logger.info("Request body: {}", ctx.body());
            }
        });

        app.after(ctx -> {
            logger.info("Response sent: [{}] {} at {}", ctx.status(), ctx.path(), java.time.LocalDateTime.now());
        });
    }


}
