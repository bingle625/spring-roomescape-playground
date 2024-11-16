package roomescape.Domains.Time;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.Domains.Reservation.Reservation;
import roomescape.exceptions.BadRequestException;

@RestController
public class TimeApiController {

  private final TimeRepository timeRepository;

  public TimeApiController(TimeRepository timeRepository) {
    this.timeRepository = timeRepository;
  }

  @GetMapping("/times")
  public ResponseEntity<List<Time>> index() {
    List<Time> times = timeRepository.findAll();

    return ResponseEntity.ok().body(times);
  }

  @PostMapping("/times")
  public ResponseEntity<Time> store(@RequestBody Object request) {
    Time newTime = new Time();
    if (request instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) request;
      newTime = new Time((String) map.get("time"));
      timeRepository.save(newTime);
    } else {
      throw new BadRequestException("ㅈ");
    }
    return ResponseEntity.created(URI.create("/times/1")).body(newTime);
  }

  @DeleteMapping("/times/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    Time time = timeRepository.findById(id)
        .orElseThrow(() -> new BadRequestException("존재하지 않는 시간입니다."));
    timeRepository.delete(time);

    return ResponseEntity.noContent().build();
  }
}
