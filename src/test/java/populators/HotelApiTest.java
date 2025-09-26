package populators;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

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
        HotelPopulator.seedDatabase();
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
}
