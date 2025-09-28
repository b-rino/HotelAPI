package app.exceptions;

public class HotelNotFoundException extends RuntimeException {
    public HotelNotFoundException(int id) {
        super("Hotel with ID " + id + " not found");
    }
}
