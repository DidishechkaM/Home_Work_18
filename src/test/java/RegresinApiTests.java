
import io.restassured.response.ValidatableResponse;
import models.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class RegresinApiTests {

    @Test
    @DisplayName("Get data of known user")
    public void getSingleUser() {
        int userId = 2;

        UserData userData = step("Make request", () -> given()
                .spec(Specs.request)
                .when()
                .get("/users/" + userId)
                .then()
                .spec(Specs.responseOk)
                .log().body()
                .extract().as(UserData.class));

        step("Verify response", () ->
                assertAll(
                        () -> assertThat(userData.getUser().getId(), equalTo(userId)),
                        () -> assertThat(userData.getUser().getEmail(), endsWith("reqres.in")),
                        () -> assertThat(userData.getUser().getAvatar(), startsWith("https://reqres.in"))
                )
        );

    }


    @Test
    @DisplayName("Successful user deletion")
    public void deleteUser() {
        int userId = 5;

        given()
                .spec(Specs.request)
                .when()
                .delete("/users/" + userId)
                .then()
                .spec(Specs.responseNoContent)
                .log().body();
    }

    @Test
    @DisplayName("Get list of users")
    public void getListOfUsers() {
        int perPage = 5;
        ValidatableResponse responce = step("Make request", () ->
                given()
                        .spec(Specs.request)
                        .when()
                        .get("/users/?per_page=" + perPage)
                        .then()
                        .spec(Specs.responseOk)
                        .log().body());

        step("Verify response", () ->
                responce.assertThat().body("data.findAll{it.email =~/.*?@reqres.in/}.email.flatten()",
                        hasItem("emma.wong@reqres.in"))
        );

    }
}
