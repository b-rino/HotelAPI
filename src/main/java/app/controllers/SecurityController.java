package app.controllers;

import app.config.HibernateConfig;
import app.daos.ISecurityDAO;
import app.daos.SecurityDAO;
import app.dtos.UserDTO;
import app.entities.Role;
import app.entities.User;
import app.exceptions.EntityAlreadyExistsException;
import app.exceptions.ValidationException;
import app.utils.TokenSecurity;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController implements ISecurityController {

    ISecurityDAO dao = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
    ObjectMapper mapper = new Utils().getObjectMapper();
    TokenSecurity tokenSecurity = new TokenSecurity();


    //Don't need to "throw exception" in method signature because the functional interface of Handler already does it! you can throw any exception in the lambda!
    @Override
    public Handler login() {
        return (Context ctx) -> {
            User user;
            try {
                user = ctx.bodyAsClass(User.class);
            } catch (Exception e) {
                throw new ValidationException("Invalid request body");
            }
            User checkedUser = dao.getVerifiedUser(user.getUsername(), user.getPassword());

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
            User incomingUser;
            try {
                incomingUser = ctx.bodyAsClass(User.class);
            } catch (Exception e) {
                throw new ValidationException("Invalid request body");
            }

            boolean isUsernameTaken = dao.existingUsername(incomingUser.getUsername());
            if(isUsernameTaken){
                throw new EntityAlreadyExistsException("Username not available");
            } else {
                User newUser = dao.createUser(incomingUser.getUsername(), incomingUser.getPassword());
                //Hard-coding all new users to "user"-role!
                User newUserWithRoles = dao.addUserRole(newUser.getUsername(), "User");
                Set<String> roleNames = newUserWithRoles.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());

                UserDTO userDTO = new UserDTO(newUserWithRoles.getUsername(), roleNames);
                String token = createToken(userDTO);

                //TODO: Maybe not a good idea to send back the token in clear text!?
                ObjectNode on = mapper.createObjectNode().put("token", token).put("Username", userDTO.getUsername());
                ctx.json(on).status(201);
            }
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
