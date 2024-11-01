package roomescape;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationInterface extends JpaRepository<Reservation, Long> {
  Reservation findByName(String name);
}
