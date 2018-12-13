package chaoxinghelper.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CHAOXING_HELPER_USER")
public class User {

    @Id
    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;

    @Column(name = "EMAIL_PASSWORD")
    private String emailPassword;

    @Column(name = "ENABLED")
    private Boolean enabled;

    @Column(name = "UPDATE_INTERVAL_SECONDS")
    private long updateIntervalSeconds;

}
