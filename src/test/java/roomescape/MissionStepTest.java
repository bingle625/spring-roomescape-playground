package roomescape;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.Domains.Reservation.Reservation;
import roomescape.Domains.Reservation.ReservationApiController;
import roomescape.Domains.Reservation.ReservationRepository;
import roomescape.Domains.Time.Time;
import roomescape.Domains.Time.TimeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MissionStepTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private TimeRepository timeRepository;
  @Autowired
  private ReservationApiController reservationController;

  @Test
  void 일단계() {
    RestAssured.given().log().all()
        .when().get("/")
        .then().log().all()
        .statusCode(200);
  }

  @Test
  void 이단계() {
    RestAssured.given().log().all()
        .when().get("/reservation")
        .then().log().all()
        .statusCode(200);

    RestAssured.given().log().all()
        .when().get("/reservations")
        .then().log().all()
        .statusCode(200)
        .body("size()", is(0));
  }

  @Test
  void 삼단계() {
    timeRepository.save(new Time("15:40"));
    Map<String, String> params = new HashMap<>();
    params.put("name", "브라운");
    params.put("date", "2023-08-05");
    params.put("time", "15:40");

    RestAssured.given().log().all()
        .contentType(ContentType.JSON)
        .body(params)
        .when().post("/reservations")
        .then().log().all()
        .statusCode(201)
        .header("Location", "/reservations/1")
        .body("id", is(1));

    RestAssured.given().log().all()
        .when().get("/reservations")
        .then().log().all()
        .statusCode(200)
        .body("size()", is(1));

    RestAssured.given().log().all()
        .when().delete("/reservations/1")
        .then().log().all()
        .statusCode(204);

    RestAssured.given().log().all()
        .when().get("/reservations")
        .then().log().all()
        .statusCode(200)
        .body("size()", is(0));
  }

  @Test
  void 사단계() {
    Map<String, String> params = new HashMap<>();
    params.put("name", "브라운");
    params.put("date", "");
    params.put("time", "");

    // 필요한 인자가 없는 경우
    RestAssured.given().log().all()
        .contentType(ContentType.JSON)
        .body(params)
        .when().post("/reservations")
        .then().log().all()
        .statusCode(400);

    // 삭제할 예약이 없는 경우
    RestAssured.given().log().all()
        .when().delete("/reservations/1")
        .then().log().all()
        .statusCode(400);
  }

  @Test
  void 오단계() {
    try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
      assertThat(connection).isNotNull();
      assertThat(connection.getCatalog()).isEqualTo("DATABASE");
      assertThat(connection.getMetaData().getTables(null, null, "RESERVATION", null)
          .next()).isTrue();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void 육단계() {

//        jdbcTemplate.update("INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)", "브라운", "2023-08-05", "15:40");
    Time newTime = timeRepository.save(new Time("15:40"));
    reservationRepository.save(new Reservation("브라운", "2023-08-05", newTime));
    List<Reservation> reservations = RestAssured.given().log().all()
        .when().get("/reservations")
        .then().log().all()
        .statusCode(200).extract()
        .jsonPath().getList(".", Reservation.class);

    Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation",
        Integer.class);

    assertThat(reservations.size()).isEqualTo(count);
  }

  @Test
  void 팔단계() {
    Map<String, String> params = new HashMap<>();
    params.put("time", "10:00");

    RestAssured.given().log().all()
        .contentType(ContentType.JSON)
        .body(params)
        .when().post("/times")
        .then().log().all()
        .statusCode(201)
        .header("Location", "/times/1");

    RestAssured.given().log().all()
        .when().get("/times")
        .then().log().all()
        .statusCode(200)
        .body("size()", is(1));

    RestAssured.given().log().all()
        .when().delete("/times/1")
        .then().log().all()
        .statusCode(204);
  }

  @Test
  void 구단계() {
    Map<String, String> reservation = new HashMap<>();
    reservation.put("name", "브라운");
    reservation.put("date", "2023-08-05");
    reservation.put("time", "10:00");

    RestAssured.given().log().all()
        .contentType(ContentType.JSON)
        .body(reservation)
        .when().post("/reservations")
        .then().log().all()
        .statusCode(400);
  }

  @Test
  void 십단계() {
    boolean isJdbcTemplateInjected = false;

    for (Field field : reservationController.getClass().getDeclaredFields()) {
      if (field.getType().equals(JdbcTemplate.class)) {
        isJdbcTemplateInjected = true;
        break;
      }
    }

    assertThat(isJdbcTemplateInjected).isFalse();
  }

}
