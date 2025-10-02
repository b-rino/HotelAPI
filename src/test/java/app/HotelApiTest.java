package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.daos.HotelDAO;
import app.daos.RoomDAO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.mappers.RoomMapper;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import populators.HotelPopulator;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;


public class HotelApiTest {
    private static Javalin app;

    @BeforeAll
    static void setUp() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        app = ApplicationConfig.startServer(7000, emf);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7000;
        RestAssured.basePath = "/api/v1";
    }

    @BeforeEach
    void populateDatabase() {
        HotelPopulator.seedHotels();
    }

    @AfterEach
    void clearDatabase() {
        HotelPopulator.clearDatabase();
    }

    @AfterAll
    static void tearDown() {
        ApplicationConfig.stopServer(app);
    }

    @Test
     void testGetAllHotels() {
        when().get("/hotel")
                .then().statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(2)));
    }

    @Test
    void testGetSpecificHotel() {
        int id = HotelPopulator.seededHotels.get(0).getId();
        given().pathParam("id", id)
                .when().get("/hotel/{id}")
                .then().statusCode(200)
                .body("id", equalTo(id));
    }

    @Test
    void testGetRoomsForHotel() {
        int id = HotelPopulator.seededHotels.get(0).getId();
        given().pathParam("id", id)
                .when().get("/hotel/{id}/rooms")
                .then().statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    void testCreateHotel() {
        given().contentType("application/json")
                .body("{\"name\":\"Test Hotel\",\"address\":\"Test Street\"}")
                .when().post("/hotel")
                .then().statusCode(201)
                .body("name", equalTo("Test Hotel"));
    }

    @Test
    void testUpdateHotel() {
        int id = HotelPopulator.seededHotels.get(0).getId();
        given().pathParam("id", id)
                .contentType("application/json")
                .body("{\"name\":\"Updated Hotel\",\"address\":\"Updated Street\"}")
                .when().put("/hotel/{id}")
                .then().statusCode(200)
                .body("name", equalTo("Updated Hotel"));
    }

    @Test
    void testDeleteHotel() {
        int id = HotelPopulator.seededHotels.get(0).getId();
        System.out.println("Trying to delete hotel with ID: " + id);
        given().pathParam("id", id)
                .when().delete("/hotel/{id}")
                .then().statusCode(anyOf(is(200), is(204)));
    }

    @Test
    void testCreateRoom() {
        int hotelId = HotelPopulator.seededHotels.get(0).getId();
        given().contentType("application/json")
                .body("{\"hotelId\":" + hotelId + ",\"number\":\"301\",\"price\":999.0}")
                .when().post("/room")
                .then().statusCode(201)
                .body("number", equalTo("301"))
                .body("price", equalTo(999.0F));
    }

    @Test
    void testDeleteRoom_success() {
        // Arrange: create a hotel and a room
        int hotelId = HotelPopulator.seededHotels.get(0).getId();
        HotelDAO hotelDAO = new HotelDAO(HibernateConfig.getEntityManagerFactoryForTest());
        Hotel hotelEntity = hotelDAO.getById(hotelId);

        RoomDTO roomDTO = RoomDTO.builder()
                .hotelId(hotelId)
                .number("999")
                .price(888.0)
                .build();

        Room roomEntity = RoomMapper.toEntity(roomDTO, hotelEntity); // sets hotel reference
        // DO NOT add room to hotel.getRooms() here — let cascade handle it

        RoomDAO roomDAO = new RoomDAO(HibernateConfig.getEntityManagerFactoryForTest());
        Room createdRoom = roomDAO.create(roomEntity); // persist only the Room

        // Act + Assert: delete the room and expect 200 OK
        given().pathParam("id", createdRoom.getId())
                .when().delete("/room/{id}")
                .then().statusCode(200)
                .body("id", equalTo(createdRoom.getId()))
                .body("number", equalTo("999"))
                .body("price", equalTo(888.0f));
    }



    @Test
    void testDeleteRoom_notFound() {
        int nonExistentRoomId = 99999;

        given().pathParam("id", nonExistentRoomId)
                .when().delete("/room/{id}")
                .then().statusCode(404)
                .body(containsString("Room not found:"));
    }

}
