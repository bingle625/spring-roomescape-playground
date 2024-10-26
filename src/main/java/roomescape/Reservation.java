package roomescape;

public class Reservation {
  private Long id;
  private String name;
  private String date;
  private String time;

  public Reservation(final Long id, final String name, final String date, final String time) {
    this.id = id;
    this.name = name;
    this.date = date;
    this.time = time;
  }

  public Long getId() {
    return id;
  }

  public String getTime() {
    return time;
  }


  public String getName() {
    return name;
  }

  public String getDate() {
    return date;
  }

  public static Reservation toEntity(Reservation reservation, Long id) {
    return new Reservation(id, reservation.name, reservation.date, reservation.time);
  }
}
