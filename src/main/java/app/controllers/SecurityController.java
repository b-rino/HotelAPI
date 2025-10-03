package app.controllers;

import app.config.HibernateConfig;
import app.daos.ISecurityDAO;
import app.daos.SecurityDAO;
import app.dtos.UserDTO;
import app.entities.Role;
import app.entities.User;
import app.exceptions.EntityAlreadyExistsException;
import app.exceptions.ValidationException;
import app.services.ISecurityService;
import app.services.SecurityService;
import app.utils.TokenSecurity;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController implements ISecurityController {


    private final ISecurityService securityService;
    private final ObjectMapper mapper;


    public SecurityController(ISecurityService securityService, ObjectMapper mapper){
        this.securityService = securityService;
        this.mapper = mapper;
    }



    //Don't need to "throw exception" in method signature because the functional interface of Handler already does it! you can throw any exception in the lambda!
    @Override
    public Handler login() {
        return (Context ctx) -> {
            User incomingUser;
            try {
                incomingUser = ctx.bodyAsClass(User.class);
            } catch (Exception e) {
                throw new ValidationException("Invalid request body");
            }
            User checkedUser = securityService.getVerifiedUser(incomingUser.getUsername(), incomingUser.getPassword());

            if (checkedUser == null) {
                throw new ValidationException("Invalid username or password");
            }
            ObjectNode on = mapper.createObjectNode().put("msg", "Login was successful");
            ctx.json(on).status(200);
        };
    }

    @Override
    public Handler register() {
        return ctx -> {
            User incomingUser = ctx.bodyValidator(User.class)
                    .check(u -> u.getUsername() != null && !u.getUsername().isBlank(), "Username is required")
                    .check(u -> u.getPassword() != null && !u.getPassword().isBlank(), "Password is required")
                    .get();

            UserDTO userDTO = securityService.register(incomingUser.getUsername(), incomingUser.getPassword());
            String token = securityService.createToken(userDTO);

            ObjectNode on = mapper.createObjectNode()
                    .put("token", token)
                    .put("Username", userDTO.getUsername());

            ctx.json(on).status(201);
        };
    }


    @Override
    public Handler authenticate() {
        return null;
    }

    @Override
    public Handler authorize() {
        return null;
    }


}
