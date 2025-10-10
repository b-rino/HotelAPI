package app.controllers;

import app.enums.RoleEnum;
import io.javalin.http.Context;

public class AccessController {

    private final SecurityController securityController;

    public AccessController(SecurityController securityController){
        this.securityController = securityController;
    }

    public void handleAccess(Context ctx){
        if(ctx.routeRoles().isEmpty() || ctx.routeRoles().contains(RoleEnum.ANYONE)) return;

        try{
            securityController.authenticate().handle(ctx);
        }
    }
}
