package chaoxinghelper.apiclient.cookie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ApiClientCookieRepository extends JpaRepository<ApiClientCookie, Long> {

    @Transactional
    List<ApiClientCookie> findByUsername(String username);

    @Transactional
    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    int deleteByUsername(String username);

    @Modifying
    @Transactional
    int deleteByExpiresAtIsLessThanEqual(long now);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update cookies c set c.expires_at = :expiresAt, c.host_only = :hostOnly, c.http_only = :httpOnly, c.secure = :secure, c.value = :value where c.domain = :domain and c.name = :name and c.path = :path and c.username = :username")
    int merge(@Param("domain") String domain, @Param("expiresAt") Long expiresAt, @Param("hostOnly") Boolean hostOnly, @Param("httpOnly") Boolean httpOnly, @Param("name") String name, @Param("path") String path, @Param("secure") Boolean secure, @Param("username") String username, @Param("value") String value);

    @Transactional
    List<ApiClientCookie> findByUsernameAndDomainAndNameAndPath(String username, String domain, String name, String path);
}
