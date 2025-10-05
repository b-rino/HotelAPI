package app.services;

import app.daos.SecurityDAO;
import app.dtos.UserDTO;
import app.entities.Role;
import app.entities.User;
import app.exceptions.*;
import app.utils.SecurityUtils;
import app.utils.Utils;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;


public class SecurityService implements ISecurityService{

    private final SecurityDAO dao;
    private final SecurityUtils securityUtils;
    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    public SecurityService(SecurityDAO dao, SecurityUtils ts){
        this.dao = dao;
        this.securityUtils = ts;
    }


    @Override
    public String createToken(UserDTO user) throws TokenCreationException {
        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");
            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }
            return securityUtils.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (TokenCreationException e) {
            logger.error("Token creation failed for user '{}': {}", user.getUsername(), e.getMessage(), e);
            throw new TokenCreationException("Could not create token", e);
        }
    }

/*    @Override
    public String getToken(Context ctx) throws TokenVerificationException {
        String header = ctx.header("Authorization");
        if (header == null || header.isBlank()) {
            logger.warn("Missing Authorization header at [{}] {}", ctx.method().toString(), ctx.path());
            throw new TokenVerificationException("Authorization header is missing");
        }

        String[] parts = header.split(" ");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("Bearer") || parts[1].isBlank()) {
            logger.warn("Malformed Authorization header at [{}] {}: '{}'", ctx.method().toString(), ctx.path(), header);
            throw new TokenVerificationException("Authorization header is malformed");
        }
        return parts[1];
    } */


    @Override
    public UserDTO verifyToken(String token) throws TokenVerificationException {
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
        String SECRET = IS_DEPLOYED
                ? System.getenv("SECRET_KEY")
                : Utils.getPropertyValue("SECRET_KEY", "config.properties");

        try {
            if (!securityUtils.tokenIsValid(token, SECRET)) {
                logger.warn("Token signature invalid for token");
                throw new TokenVerificationException("Token signature is invalid");
            }

            if (!securityUtils.tokenNotExpired(token)) {
                logger.warn("Token expired for token");
                throw new TokenVerificationException("Token has expired");
            }

            return securityUtils.getUserWithRolesFromToken(token);

        } catch (ParseException | TokenVerificationException e) {
            logger.warn("Token verification failed: {}", e.getMessage(), e);
            throw new TokenVerificationException("Could not verify token", e);
        }
    }

    @Override
    public String getToken(Context ctx) throws TokenVerificationException {
        String header = ctx.header("Authorization");
        if (header == null || header.isBlank()) {
            logger.warn("Missing Authorization header at [{}] {}", ctx.method().toString(), ctx.path());
            throw new TokenVerificationException("Authorization header is missing");
        }

        String[] parts = header.split(" ");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("Bearer") || parts[1].isBlank()) {
            logger.warn("Malformed Authorization header at [{}] {}: '{}'", ctx.method().toString(), ctx.path(), header);
            throw new TokenVerificationException("Authorization header is malformed");
        }
        return parts[1];
    }


    @Override
    public UserDTO validateAndGetUserFromToken(Context ctx) throws TokenVerificationException  {
        String token = getToken(ctx);
        UserDTO verifiedTokenUser = verifyToken(token);

        if (verifiedTokenUser == null) {
            logger.warn("Token verified but no user found at [{}] {}", ctx.method().toString(), ctx.path());
            throw new TokenVerificationException("Token is valid but user could not be resolved");
        }
        return verifiedTokenUser;
    }

    public boolean userHasAllowedRole(UserDTO user, Set<String> allowedRoles) {
        if (user == null || user.getRoles() == null || allowedRoles == null || allowedRoles.isEmpty()) {
            return false;
        }

        return user.getRoles().stream()
                .map(String::toUpperCase)
                .anyMatch(allowedRoles::contains);
    }



    public User getVerifiedUser(String username, String password) throws ValidationException {
        try{
            return dao.getVerifiedUser(username, password);
        } catch (Exception e){
            throw new ValidationException("Invalid username or password");
        }
    }

    public boolean existingUsername(String username){
        return dao.existingUsername(username);
    }

    public UserDTO register(String username, String password) throws EntityAlreadyExistsException, EntityNotFoundException {
        if (dao.existingUsername(username)) {
            throw new EntityAlreadyExistsException("Username not available");
        }

        User newUser = dao.createUser(username, password);
        User newUserWithRoles;
        try{
            newUserWithRoles = dao.addUserRole(newUser.getUsername(), "User");
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("Either user or role doesn't exist!");
        }

        Set<String> roleNames = newUserWithRoles.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        return new UserDTO(newUserWithRoles.getUsername(), roleNames);
    }

}
