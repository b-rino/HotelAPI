package app.config;

import app.routes.Routes;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

public class ApplicationConfig {


    public static Javalin startServer(int port, EntityManagerFactory emf) {
        Routes routes = new Routes(emf);
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.bundledPlugins.enableRouteOverview("/routes");
            config.router.contextPath = "/api/v1";
            config.router.apiBuilder(routes.getRoutes());
        });
        app.start(port);
        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
