package chaoxinghelper.apiclient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "CHAOXING_TASKS")
@NoArgsConstructor
public class ChaoxingTask implements Cloneable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "COURSE")
    private String course;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DETAIL")
    private String detail;

    @Column(name = "START_DATE_TIME")
    private Date startDateTime;

    @Column(name = "DUE_DATE_TIME")
    private Date dueDateTime;

    @Column(name = "PUSHED")
    private Boolean pushed = false;

    @java.beans.ConstructorProperties({"id", "username", "course", "name", "detail", "startDateTime", "dueDateTime"})
    public ChaoxingTask(Long id, String username, String course, String name, String detail, Date startDateTime, Date dueDateTime) {
        this.id = id;
        this.username = username;
        this.course = course;
        this.name = name;
        this.detail = detail;
        this.startDateTime = startDateTime;
        this.dueDateTime = dueDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChaoxingTask)) return false;
        ChaoxingTask that = (ChaoxingTask) o;
        return getId().equals(that.getId()) &&
                getUsername().equals(that.getUsername()) &&
                getCourse().equals(that.getCourse()) &&
                getName().equals(that.getName()) &&
                getDetail().equals(that.getDetail()) &&
                getStartDateTime().equals(that.getStartDateTime()) &&
                getDueDateTime().equals(that.getDueDateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getCourse(), getName(), getDetail(), getStartDateTime(), getDueDateTime());
    }
}
