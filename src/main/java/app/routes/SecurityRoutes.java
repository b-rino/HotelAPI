package app.routes;

import app.controllers.SecurityController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class SecurityRoutes {

    private SecurityController controller = new SecurityController();

    //functional style route registration because we use "Handler" in controller
    //good for authentication and easy to pass around!
    public EndpointGroup getRoutes() {
        return () -> {
            post("login", controller.login());
        };
    }
}
