package roomescape.Domains.Reservation;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.Domains.Time.Time;
import roomescape.Domains.Time.TimeRepository;
import roomescape.exceptions.BadRequestException;

@RestController
public class ReservationApiController {

  @Autowired
  private ReservationRepository reservationRepository;
  @Autowired
  private TimeRepository timeRepository;

  @GetMapping("/reservations")
  public ResponseEntity<List<Reservation>> getReservations() {
    List<Reservation> reservations = this.reservationRepository.findAll();
    return ResponseEntity.ok().body(reservations);
  }

  @PostMapping("/reservations")
  public ResponseEntity<Reservation> create(@RequestBody ReservationStoreRequestDto requestDto) {
    if (Objects.isNull(requestDto.date()) || requestDto.date().isEmpty() ||
        Objects.isNull(requestDto.time()) || requestDto.time().isEmpty() ||
        Objects.isNull(requestDto.name()) || requestDto.name().isEmpty()) {
      throw new BadRequestException("올바르지 않은 입력입니다.");
    }
    Time time = timeRepository.findFirstByTime(requestDto.time())
        .orElseThrow(() -> new BadRequestException("존재하지 않는 시간입니다."));
    Reservation newReservation = reservationRepository.save(
        new Reservation(requestDto.name(), requestDto.date(), time));

    return ResponseEntity.created(URI.create("/reservations/" + newReservation.getId()))
        .body(newReservation);
  }

  @DeleteMapping("/reservations/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    Reservation reservation = this.reservationRepository.findById(id)
        .orElseThrow(() -> new BadRequestException("존재하지 않는 예약입니다."));
    this.reservationRepository.delete(reservation);

    return ResponseEntity.noContent().build();
  }
}
