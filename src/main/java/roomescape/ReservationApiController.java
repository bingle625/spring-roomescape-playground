package roomescape;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationApiController {

  private final List<Reservation> reservations = new ArrayList<>();

  @GetMapping("/reservations")
  public ResponseEntity<List<Reservation>> getReservations() {
    this.reservations.add(new Reservation(1L, "브라운", "2023-01-01", "10:00"));
    this.reservations.add(new Reservation(2L, "브라운", "2023-01-02", "11:00"));

    return ResponseEntity.ok().body(this.reservations);
  }
}
