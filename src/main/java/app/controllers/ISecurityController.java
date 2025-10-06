package app.controllers;

import app.exceptions.ValidationException;
import io.javalin.http.Handler;

public interface ISecurityController {

    Handler login(); // to get a token after checking username and password
    Handler register(); // to make a new User and get a token
    Handler authenticate(); // to verify that a token was sent with the request and that it is a valid, non-expired token
    Handler authorize(); // to verify user roles
}
