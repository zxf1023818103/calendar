package chaoxinghelper.apiclient.cookie;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "COOKIES")
@ToString
@NoArgsConstructor
final class ApiClientCookie implements Cloneable {

    @Id
    @Getter
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Getter
    @Setter
    @Column(name = "USERNAME")
    private String username;

    @Getter
    @Setter
    @Column(name = "DOMAIN")
    private String domain;

    @Getter
    @Setter
    @Column(name = "EXPIRES_AT")
    private Long expiresAt;

    @Getter
    @Setter
    @Column(name = "HOST_ONLY")
    private Boolean hostOnly;

    @Getter
    @Setter
    @Column(name = "HTTP_ONLY")
    private Boolean httpOnly;

    @Getter
    @Setter
    @Column(name = "NAME")
    private String name;

    @Getter
    @Setter
    @Column(name = "PATH")
    private String path;

    @Getter
    @Setter
    @Column(name = "SECURE")
    private Boolean secure;

    @Getter
    @Setter
    @Column(name = "VALUE", length = 1024)
    private String value;

    ApiClientCookie(String username, String domain, Long expiresAt, Boolean hostOnly, Boolean httpOnly, String name, String path, Boolean secure, String value) {
        this.username = username;
        this.domain = domain;
        this.expiresAt = expiresAt;
        this.hostOnly = hostOnly;
        this.httpOnly = httpOnly;
        this.name = name;
        this.path = path;
        this.secure = secure;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiClientCookie)) return false;
        ApiClientCookie that = (ApiClientCookie) o;
        return getId().equals(that.getId()) &&
                getUsername().equals(that.getUsername()) &&
                getDomain().equals(that.getDomain()) &&
                getExpiresAt().equals(that.getExpiresAt()) &&
                getHostOnly().equals(that.getHostOnly()) &&
                getHttpOnly().equals(that.getHttpOnly()) &&
                getName().equals(that.getName()) &&
                getPath().equals(that.getPath()) &&
                getSecure().equals(that.getSecure()) &&
                getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getDomain(), getExpiresAt(), getHostOnly(), getHttpOnly(), getName(), getPath(), getSecure(), getValue());
    }
}
