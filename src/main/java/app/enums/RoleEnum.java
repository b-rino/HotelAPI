package app.enums;

import io.javalin.security.RouteRole;

public enum RoleEnum implements RouteRole {
    ANYONE, USER, ADMIN
}
