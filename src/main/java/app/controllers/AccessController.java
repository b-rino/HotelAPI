package app.controllers;

import app.dtos.UserDTO;
import app.enums.RoleEnum;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

public class AccessController {

    private final SecurityController securityController;

    public AccessController(SecurityController securityController) {
        this.securityController = securityController;
    }

    public void accessHandler(Context ctx) {

        //TODO: Blot for at teste om jeg får fat i rollerne korrekt
        System.out.println("Route roles: " + ctx.routeRoles());


        if (ctx.routeRoles().isEmpty() || ctx.routeRoles().contains(RoleEnum.ANYONE)) return;

        try {
            securityController.authenticate().handle(ctx);
        } catch (Exception e) {
            throw new UnauthorizedResponse("Invalid or missing token!");
        }

        UserDTO userDTO = ctx.attribute("user");

        if (userDTO == null) {
            throw new UnauthorizedResponse("Missing user from token!");
        }

        try {
            securityController.authorize().handle(ctx);
        } catch (Exception e) {
            throw new UnauthorizedResponse("User not authorized for this route!");
        }
    }
}
