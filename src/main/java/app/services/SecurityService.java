package app.services;

import app.daos.SecurityDAO;
import app.dtos.UserDTO;
import app.exceptions.TokenCreationException;
import app.exceptions.TokenVerificationException;
import app.utils.TokenSecurity;
import app.utils.Utils;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Set;

public class SecurityService {

    private final SecurityDAO dao;
    private final TokenSecurity tokenSecurity;
    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    public SecurityService(SecurityDAO dao, TokenSecurity ts){
        this.dao = dao;
        this.tokenSecurity = ts;
    }


    private String createToken(UserDTO user) throws TokenCreationException {
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
            return tokenSecurity.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (TokenCreationException e) {
            logger.error("Token creation failed for user '{}': {}", user.getUsername(), e.getMessage(), e);
            throw new TokenCreationException("Could not create token", e);
        }
    }


    private static String getToken(Context ctx) throws TokenVerificationException {
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


    private UserDTO verifyToken(String token) throws TokenVerificationException {
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
        String SECRET = IS_DEPLOYED
                ? System.getenv("SECRET_KEY")
                : Utils.getPropertyValue("SECRET_KEY", "config.properties");

        try {
            if (!tokenSecurity.tokenIsValid(token, SECRET)) {
                logger.warn("Token signature invalid for token");
                throw new TokenVerificationException("Token signature is invalid");
            }

            if (!tokenSecurity.tokenNotExpired(token)) {
                logger.warn("Token expired for token");
                throw new TokenVerificationException("Token has expired");
            }

            return tokenSecurity.getUserWithRolesFromToken(token);

        } catch (ParseException | TokenVerificationException e) {
            logger.warn("Token verification failed: {}", e.getMessage(), e);
            throw new TokenVerificationException("Could not verify token", e);
        }
    }




    private UserDTO validateAndGetUserFromToken(Context ctx) throws TokenVerificationException  {
        String token = getToken(ctx);
        UserDTO verifiedTokenUser = verifyToken(token);

        if (verifiedTokenUser == null) {
            logger.warn("Token verified but no user found at [{}] {}", ctx.method().toString(), ctx.path());
            throw new TokenVerificationException("Token is valid but user could not be resolved");
        }
        return verifiedTokenUser;
    }

    private static boolean userHasAllowedRole(UserDTO user, Set<String> allowedRoles) {
        if (user == null || user.getRoles() == null || allowedRoles == null || allowedRoles.isEmpty()) {
            return false;
        }

        return user.getRoles().stream()
                .map(String::toUpperCase)
                .anyMatch(allowedRoles::contains);
    }


    private boolean isOpenEndpoint(Set<String> allowedRoles) {
        // If the endpoint is not protected with any roles:
        if (allowedRoles.isEmpty())
            return true;

        // 1. Get permitted roles and Check if the endpoint is open to all with the ANYONE role
        if (allowedRoles.contains("ANYONE")) {
            return true;
        }
        return false;
    }

}
