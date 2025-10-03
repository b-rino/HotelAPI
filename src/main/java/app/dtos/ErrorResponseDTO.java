package app.dtos;

public class ErrorResponseDTO {

    public String error;
    public String message;
    public String path;
    public String method;

    public ErrorResponseDTO(String error, String message, String path, String method) {
        this.error = error;
        this.message = message;
        this.path = path;
        this.method = method;
    }
}
