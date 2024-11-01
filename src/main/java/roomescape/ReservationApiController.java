package roomescape;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.exceptions.BadRequestException;

@RestController
public class ReservationApiController {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private ReservationRepository reservationRepository;

  @GetMapping("/reservations")
  public ResponseEntity<List<Reservation>> getReservations() {
    List<Reservation> reservations = jdbcTemplate.query("select * from reservation",
        ((rs, rowNum) -> new Reservation(rs.getLong("id"), rs.getString("name"),
            rs.getString("date"), rs.getString("time"))));
    return ResponseEntity.ok().body(reservations);
  }

  @PostMapping("/reservations")
  public ResponseEntity<Reservation> create(@RequestBody Reservation reservation) {
    if (Objects.isNull(reservation.getDate()) || reservation.getDate().isEmpty() ||
        Objects.isNull(reservation.getTime()) || reservation.getTime().isEmpty() ||
        Objects.isNull(reservation.getName()) || reservation.getName().isEmpty()) {
      throw new BadRequestException("올바르지 않은 입력입니다.");
    }

    Reservation newReservation =  this.reservationRepository.save(reservation);

    return ResponseEntity.created(URI.create("/reservations/" + newReservation.getId()))
        .body(newReservation);
  }

  @DeleteMapping("/reservations/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    int count = this.jdbcTemplate.queryForObject(
        "select count(*) from reservation where id = ?", new Object[]{id}, int.class);

    if (count == 0) {
      throw new BadRequestException("존재하지 않는 예약입니다.");
    }

    this.jdbcTemplate.update("delete from reservation where id = ?", id);

    return ResponseEntity.noContent().build();
  }
}
