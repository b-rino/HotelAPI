package app.utils;

import app.dtos.UserDTO;

import app.exceptions.TokenCreationException;
import app.exceptions.TokenVerificationException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;


//Class originally from Hartmann's dependency. Just copied it to my own project!
public class TokenSecurity {
    public UserDTO getUserWithRolesFromToken(String token) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(token);
        String roles = jwt.getJWTClaimsSet().getClaim("roles").toString();
        String username = jwt.getJWTClaimsSet().getClaim("username").toString();
        Set<String> rolesSet = (Set) Arrays.stream(roles.split(",")).collect(Collectors.toSet());
        return new UserDTO(username, rolesSet);
    }

    public boolean tokenIsValid(String token, String secret) throws ParseException, TokenVerificationException {
        boolean verified = false;

        try {
            SignedJWT jwt = SignedJWT.parse(token);
            verified = jwt.verify(new MACVerifier(secret));
        } catch (JOSEException e) {
            throw new TokenVerificationException("Could not verify token", e.getCause());
        }

        return verified;
    }

    public boolean tokenNotExpired(String token) throws ParseException {
        return this.timeToExpire(token) > 0;
    }

    public int timeToExpire(String token) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(token);
        return (int) (jwt.getJWTClaimsSet().getExpirationTime().getTime() - (new Date()).getTime());
    }

    public String createToken(UserDTO user, String ISSUER, String TOKEN_EXPIRE_TIME, String SECRET_KEY) throws TokenCreationException {
        try {
            JWTClaimsSet claimsSet = (new JWTClaimsSet.Builder()).subject(user.getUsername()).issuer(ISSUER).claim("username", user.getUsername()).claim("roles", user.getRoles().stream().reduce((s1, s2) -> s1 + "," + s2).get()).expirationTime(new Date((new Date()).getTime() + (long) Integer.parseInt(TOKEN_EXPIRE_TIME))).build();
            Payload payload = new Payload(claimsSet.toJSONObject());
            JWSSigner signer = new MACSigner(SECRET_KEY);
            JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (JOSEException e) {
            e.printStackTrace();
            throw new TokenCreationException("Could not create token", e);
        }
    }
}
