package io.plantya.iot.device.controller;

import io.plantya.iot.device.dto.request.DeviceCreateRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Disabled
@QuarkusTest
@DisplayName("DeviceController Integration Test")
class DeviceControllerTest {

    // =========================================================
    // CREATE
    // =========================================================

    @Nested
    @DisplayName("POST /api/devices")
    class CreateDevice {

        @Test
        @DisplayName("201 - create device successfully")
        void createDevice_success() {
            DeviceCreateRequest request =
                    new DeviceCreateRequest("device-1", "sensor", "cluster-1");

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/devices")
                    .then()
                    .statusCode(201)
                    .body("device_name", equalTo("device-1"))
                    .body("device_type", equalTo("sensor"))
                    .body("cluster_id", equalTo("cluster-1"));
        }

        @Test
        @DisplayName("400 - missing device name")
        void createDevice_missingName() {
            DeviceCreateRequest request =
                    new DeviceCreateRequest(null, "sensor", "cluster");

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/devices")
                    .then()
                    .statusCode(400)
                    .body("code", equalTo("DEVICE_NAME_REQUIRED"));
        }
    }

    // =========================================================
    // GET
    // =========================================================

    @Nested
    @DisplayName("GET /api/devices/{id}")
    class GetDevice {

        @Test
        @DisplayName("200 - get existing device")
        void getDevice_success() {
            String id =
                    given()
                            .contentType(ContentType.JSON)
                            .body(new DeviceCreateRequest("dev", "sensor", "cluster"))
                            .when()
                            .post("/api/devices")
                            .then()
                            .statusCode(201)
                            .extract()
                            .path("device_id");

            given()
                    .when()
                    .get("/api/devices/{id}", id)
                    .then()
                    .statusCode(200)
                    .body("device_id", equalTo(id));
        }

        @Test
        @DisplayName("404 - device not found")
        void getDevice_notFound() {
            given()
                    .when()
                    .get("/api/devices/unknown-id")
                    .then()
                    .statusCode(404)
                    .body("code", equalTo("DEVICE_NOT_FOUND"));
        }
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Nested
    @DisplayName("DELETE /api/devices/{id}")
    class DeleteDevice {

        @Test
        @DisplayName("204 - delete device")
        void deleteDevice_success() {
            String id =
                    given()
                            .contentType(ContentType.JSON)
                            .body(new DeviceCreateRequest("to-delete", "sensor", "cluster"))
                            .when()
                            .post("/api/devices")
                            .then()
                            .statusCode(201)
                            .extract()
                            .path("device_id");

            given()
                    .when()
                    .delete("/api/devices/{id}", id)
                    .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("409 - already deleted")
        void deleteDevice_alreadyDeleted() {
            String id =
                    given()
                            .contentType(ContentType.JSON)
                            .body(new DeviceCreateRequest("temp", "sensor", "cluster"))
                            .when()
                            .post("/api/devices")
                            .then()
                            .statusCode(201)
                            .extract()
                            .path("device_id");

            // delete once
            given().delete("/api/devices/{id}", id).then().statusCode(204);

            // delete again
            given()
                    .when()
                    .delete("/api/devices/{id}", id)
                    .then()
                    .statusCode(409)
                    .body("code", equalTo("DEVICE_ALREADY_DELETED"));
        }
    }
}
