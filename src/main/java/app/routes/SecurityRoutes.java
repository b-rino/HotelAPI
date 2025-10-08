package app.routes;

import app.controllers.SecurityController;
import app.daos.SecurityDAO;
import app.services.ISecurityService;
import app.services.SecurityService;
import app.utils.SecurityUtils;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class SecurityRoutes {

    private final SecurityController securityController;

    public SecurityRoutes(EntityManagerFactory emf){
        SecurityDAO dao = new SecurityDAO(emf);
        SecurityUtils securityUtils = new SecurityUtils();
        ISecurityService securityService = new SecurityService(dao, securityUtils);
        ObjectMapper mapper = new Utils().getObjectMapper();

        this.securityController = new SecurityController(securityService, mapper);
    }


    public EndpointGroup getRoutes() {
        return () -> {
            post("login", securityController.login());
            post("register", securityController.register());
            get("healthcheck", securityController::healthCheck);
            get("dockerhub", securityController::dockerHub);
        };
    }


    public EndpointGroup getSecuredRoutes() {
        return () -> {
            get("authenticate", securityController.authenticate());
            get("authorize", securityController.authorize());
        };
    }

}
