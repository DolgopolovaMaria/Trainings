package tests;

import io.qameta.allure.Feature;
import io.restassured.response.Response;
import models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static helpers.Specs.request;
import static helpers.Specs.responseSpec;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("API tests")
public class ApiTests {
    @Test
    @DisplayName("Get list users")
    @Feature("Get")
    void getListUsersTest(){
        step("GET /api/users?page=2", () -> {
            given()
                    .spec(request)
                    .when()
                    .get("/users?page=2")
                    .then()
                    .spec(responseSpec)
                    .log().status()
                    .log().body()
                    .statusCode(200)
                    .body("total", is(12))
                    .body("data.first_name", hasItems("Michael", "Tobias", "Rachel"));
        });
    }

    @Test
    @DisplayName("Create user")
    @Feature("Post")
    void postCreateTest(){
        String body = "{ \"name\": \"michel\", \"job\": \"leader\" }";

        step("POST /api/users", () -> {
            User data = given()
                .spec(request)
                .when()
                .body(body)
                .contentType(JSON)
                .post("/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .extract().as(User.class);

            assertEquals("leader", data.getJob());
        });

    }

    @Test
    @DisplayName("Delete user")
    @Feature("Delete")
    void deleteUserTest(){
        step("DELETE /api/users/2", () -> {
        given()
                .spec(request)
                .when()
                .delete("/users/2")
                .then()
                .log().status()
                .statusCode(204);
        });
    }

    @Test
    @DisplayName("Unsuccessful login")
    @Feature("Post")
    void postUnsuccessfulLoginTest(){
        String body = "{\"email\": \"test@test.com\"}";

        step("POST /api/login", () -> {
        given()
                .spec(request)
                .when()
                .body(body)
                .contentType(JSON)
                .post("/login")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing password"));
        });
    }

    @Test
    @DisplayName("Get nonexistent user")
    @Feature("Get")
    void getNotFoundTest(){
        step("GET /api/users/23", () -> {
        Response response = given()
                .spec(request)
                .when()
                .get("/users/23")
                .then()
                .log().status()
                .log().body()
                .statusCode(404)
                .extract().response();

        assertEquals("{}", response.asString());
        });
    }
}
