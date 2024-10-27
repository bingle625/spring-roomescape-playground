package roomescape;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.exceptions.BadRequestException;

@RestController
public class ReservationApiController {

  private final List<Reservation> reservations = new ArrayList<>();
  private AtomicLong index = new AtomicLong(1);

  @GetMapping("/reservations")
  public ResponseEntity<List<Reservation>> getReservations() {
    return ResponseEntity.ok().body(this.reservations);
  }

  @PostMapping("/reservations")
  public ResponseEntity<Reservation> create(@RequestBody Reservation reservation) {
    if (reservation.getDate().isEmpty() || reservation.getTime().isEmpty() || reservation.getName().isEmpty()) {
      throw new BadRequestException("올바르지 않은 입력입니다.");
    }
    Reservation newReservation = Reservation.toEntity(reservation, index.getAndIncrement());
    reservations.add(newReservation);
    return ResponseEntity.created(URI.create("/reservations/" + this.reservations.size()))
        .body(newReservation);
  }

  @DeleteMapping("/reservations/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    Reservation targetReservation = reservations.stream()
        .filter(target -> Objects.equals(target.getId(), id))
        .findFirst()
        .orElseThrow(()-> new BadRequestException("존재하지 않는 예약입니다."));

    reservations.remove(targetReservation);
    return ResponseEntity.noContent().build();
  }
}
