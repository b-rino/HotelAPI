package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.daos.HotelDAO;
import app.daos.RoomDAO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.exceptions.EntityNotFoundException;
import app.mappers.RoomMapper;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import populators.Populator;

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
    void populateDatabase() throws EntityNotFoundException {
        Populator.seedHotels();
        Populator.seedRoles();
        Populator.seedUsers();
    }

    @AfterEach
    void clearDatabase() {
        Populator.clearDatabase();
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
        int id = Populator.seededHotels.get(0).getId();
        given().pathParam("id", id)
                .when().get("/hotel/{id}")
                .then().statusCode(200)
                .body("id", equalTo(id));
    }

    @Test
    void testGetRoomsForHotel() {
        int id = Populator.seededHotels.get(0).getId();
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
        int id = Populator.seededHotels.get(0).getId();
        given().pathParam("id", id)
                .contentType("application/json")
                .body("{\"name\":\"Updated Hotel\",\"address\":\"Updated Street\"}")
                .when().put("/hotel/{id}")
                .then().statusCode(200)
                .body("name", equalTo("Updated Hotel"));
    }

    @Test
    void testDeleteHotel() {
        int id = Populator.seededHotels.get(0).getId();
        System.out.println("Trying to delete hotel with ID: " + id);
        given().pathParam("id", id)
                .when().delete("/hotel/{id}")
                .then().statusCode(anyOf(is(200), is(204)));
    }

    @Test
    void testCreateRoom() {
        int hotelId = Populator.seededHotels.get(0).getId();
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
        int hotelId = Populator.seededHotels.get(0).getId();
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
                .body(containsString("Room not found"));
    }




    private String loginAndGetToken(String username, String password) {
        return given().contentType("application/json")
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .when().post("/auth/login")
                .then().statusCode(200)
                .extract().path("token");
    }

    @Test
    void testUserProtectedEndpoint_allowed() {
        String token = loginAndGetToken("User", "1234");

        given().header("Authorization", "Bearer " + token)
                .when().get("/protected/user_demo")
                .then().statusCode(200)
                .body("msg", equalTo("Hello from USER Protected"));
    }

    @Test
    void testUserProtectedEndpoint_notAllowed() {
        given().header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJcIkJlbmphbWluIFJpbm9cIiIsInN1YiI6ImFkbWluIiwiZXhwIjoxNzYwNDY3MDQwLCJyb2xlcyI6IkFkbWluIiwidXNlcm5hbWUiOiJhZG1pbiJ9.B68gZi87uQRb5Q5BlRS-wAtXPy4W0fAhfm6z5ii67eY")
                .when().get("/protected/user_demo")
                .then().statusCode(401)
                .body("error", equalTo("Token verification failed"));
    }

    @Test
    void testUserProtectedEndpoint_invalidSignature() {
        String tamperedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOiJVc2VyIiwiZXhwIjoyNzYwNDY3MDQwfQ.fakeSignature";

        given().header("Authorization", "Bearer " + tamperedToken)
                .when().get("/protected/user_demo")
                .then().statusCode(401)
                .body("error", equalTo("Token verification failed"))
                .body("message", equalTo("Token signature is invalid"));
    }

    @Test
    void testUserProtectedEndpoint_malformedToken() {
        String malformedToken = "this.is.not.a.valid.jwt";

        given().header("Authorization", "Bearer " + malformedToken)
                .when().get("/protected/user_demo")
                .then().statusCode(401)
                .body("error", equalTo("Token verification failed"))
                .body("message", containsString("Token is malformed"));
    }

    @Test
    void testUserProtectedEndpoint_missingToken() {
        given()
                .when().get("/protected/user_demo")
                .then().statusCode(401)
                .body("error", equalTo("Token verification failed"))
                .body("message", equalTo("Authorization header is missing"));
    }

    @Test
    void testAdminProtectedEndpoint_allowed() {
        String token = loginAndGetToken("Admin", "1234");

        given().header("Authorization", "Bearer " + token)
                .when().get("/protected/admin_demo")
                .then().statusCode(200)
                .body("msg", equalTo("Hello from ADMIN Protected"));
    }

    @Test
    void testAdminProtectedEndpoint_wrongRole() {
        String userToken = loginAndGetToken("User", "1234");

        given().header("Authorization", "Bearer " + userToken)
                .when().get("/protected/admin_demo")
                .then().statusCode(400); //Bruger Javalins standard ValidationException i denne Route som kaster 400
    }



    @Test
    void testRegister_validUser() {
        given().contentType("application/json")
                .body("{\"username\":\"newuser\",\"password\":\"securepass\"}")
                .when().post("/auth/register")
                .then().statusCode(201)
                .body("token", notNullValue())
                .body("Username", equalTo("newuser"));
    }

    @Test
    void testRegister_duplicateUsername() {
        // Først registrer brugeren
        given().contentType("application/json")
                .body("{\"username\":\"existinguser\",\"password\":\"pass\"}")
                .when().post("/auth/register")
                .then().statusCode(201);

        // Forsøg igen med samme brugernavn
        given().contentType("application/json")
                .body("{\"username\":\"existinguser\",\"password\":\"pass\"}")
                .when().post("/auth/register")
                .then().statusCode(409)
                .body("error", equalTo("Entity already exists"))
                .body("message", containsString("Username not available"));
    }



}
