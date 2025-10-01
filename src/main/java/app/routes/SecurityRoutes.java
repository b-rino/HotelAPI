package app.routes;

import app.controllers.SecurityController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class SecurityRoutes {

    private SecurityController controller = new SecurityController();

    public EndpointGroup getRoutes() {
        return () -> {
            post("login", controller.login());
        };
    }
}
