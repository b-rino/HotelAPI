package app.exceptions;

public class TokenVerificationException extends Exception {
    public TokenVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenVerificationException(String message){
        super(message);
    }
}
