package app.exceptions;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(int id) {
        super("Room with ID " + id + " not found");
    }
}
