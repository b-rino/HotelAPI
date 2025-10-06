package app.services;

import app.dtos.UserDTO;
import app.entities.User;
import app.exceptions.*;
import io.javalin.http.Context;

import java.util.Set;

public interface ISecurityService {

    String createToken(UserDTO user) throws TokenCreationException;

    String getToken(Context ctx) throws TokenVerificationException;

    UserDTO verifyToken(String token) throws TokenVerificationException;

    UserDTO validateAndGetUserFromToken(Context ctx) throws TokenVerificationException;

    boolean userHasAllowedRole(UserDTO user, Set<String> allowedRoles);

    User getVerifiedUser(String username, String password) throws ValidationException;

    boolean existingUsername(String username);

    UserDTO register(String username, String password) throws EntityAlreadyExistsException, EntityNotFoundException;

}
