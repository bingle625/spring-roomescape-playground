package roomescape.Domains.Time;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Time {

  public Time() {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String time;
}
