package app.routes;

import app.controllers.SecurityController;
import app.daos.SecurityDAO;
import app.services.ISecurityService;
import app.services.SecurityService;
import app.utils.TokenSecurity;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class SecurityRoutes {

    private final SecurityController securityController;

    public SecurityRoutes(EntityManagerFactory emf){
        SecurityDAO dao = new SecurityDAO(emf);
        TokenSecurity tokenSecurity = new TokenSecurity();
        ISecurityService securityService = new SecurityService(dao, tokenSecurity);
        ObjectMapper mapper = new Utils().getObjectMapper();

        this.securityController = new SecurityController(securityService, mapper);
    }

    //functional style route registration because we use "Handler" in controller
    //good for authentication and easy to pass around!
    public EndpointGroup getRoutes() {
        return () -> {
            post("login", securityController.login());
            post("register", securityController.register());
            get("authenticate", securityController.authenticate());
            get("authorize", securityController.authorize());
        };
    }
}
