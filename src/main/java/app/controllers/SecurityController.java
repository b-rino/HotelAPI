package app.controllers;

import app.config.HibernateConfig;
import app.daos.ISecurityDAO;
import app.daos.SecurityDAO;
import app.entities.User;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class SecurityController {

    ISecurityDAO dao = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
    ObjectMapper mapper = new Utils().getObjectMapper();


    public Handler login() {
        return (Context ctx) -> {
            User user = ctx.bodyAsClass(User.class);
            User checkedUser = dao.getVerifiedUser(user.getUsername(), user.getPassword());
            System.out.println("Successfully logged in: " + checkedUser.getUsername());
            ObjectNode on = mapper.createObjectNode().put("msg", "login was successful");
            ctx.json(on).status(200);
        };
    }
}
