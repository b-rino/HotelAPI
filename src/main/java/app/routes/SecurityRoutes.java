package app.routes;

import app.controllers.SecurityController;
import app.daos.SecurityDAO;
import app.enums.RoleEnum;
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
    private final ObjectMapper mapper;

    public SecurityRoutes(EntityManagerFactory emf){
        SecurityDAO dao = new SecurityDAO(emf);
        SecurityUtils securityUtils = new SecurityUtils();
        ISecurityService securityService = new SecurityService(dao, securityUtils);
        this.mapper = new Utils().getObjectMapper();

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

            get("user_demo", (ctx)->ctx.json(mapper.createObjectNode().put("msg", "Hello from USER Protected")), RoleEnum.USER);
            get("admin_demo", (ctx)->ctx.json(mapper.createObjectNode().put("msg", "Hello from ADMIN Protected")), RoleEnum.ADMIN);
            get("both_demo", (ctx)->ctx.json(mapper.createObjectNode().put("msg", "Hello from both Protected")), RoleEnum.ADMIN, RoleEnum.USER);
        };
    }

    public SecurityController getSecurityController() {
        return securityController;
    }
}
