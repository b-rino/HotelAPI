package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import populators.HotelPopulator;
import populators.UserPopulator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.hamcrest.Matchers.*;
//TODO: Klassen er ikke lavet endnu - blot kopieret fra et andet projekt!!
/*public class UserApiTest {

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
        UserPopulator.seedUsers();
    }

    @AfterEach
    void clearDatabase() {
        UserPopulator.clearDatabase();
    }

    @AfterAll
    static void tearDown() {
        ApplicationConfig.stopServer(app);
    }


    //TODO: Not created yet! just copied from another project!
    @Test
    public void testLoginForSeededUser() {
        given()
                .contentType("application/json")
                .body("{\"username\": \"Benjamin\", \"password\": \"1234\"}")
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200);
    }
}*/
