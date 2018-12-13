package chaoxinghelper.apiclient.cookie;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class ApiClientCookieJar implements CookieJar {

    private final ApiClientCookieRepository cookieRepository;

    @Getter
    private String username;

    public ApiClientCookieJar(String username, ApiClientCookieRepository cookieRepository) {
        this.username = username;
        this.cookieRepository = cookieRepository;
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        /// 删除过期的 cookie
        cookieRepository.deleteByExpiresAtIsLessThanEqual(System.currentTimeMillis());
        cookieRepository.flush();
        LinkedList<Cookie> results = new LinkedList<>();
        cookieRepository.findByUsername(username).forEach(cookie -> {
            var builder = new Cookie.Builder();
            if (cookie.getHostOnly())
                builder.hostOnlyDomain(cookie.getDomain());
            else
                builder.domain(cookie.getDomain());
            if (cookie.getHttpOnly())
                builder.httpOnly();
            if (cookie.getSecure())
                builder.secure();
            builder.path(cookie.getPath())
                    .value(cookie.getValue())
                    .name(cookie.getName())
                    .expiresAt(cookie.getExpiresAt());
            results.offer(builder.build());
        });
        log.debug("Loaded " + results.size() + " cookie(s)");
        return results;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookies.forEach(cookie -> {
            var domain = cookie.domain();
            var name = cookie.name();
            var path = cookie.path();
            var existingCookie = cookieRepository.findByUsernameAndDomainAndNameAndPath(username, domain, name, path);
            if (existingCookie.size() != 0)
                cookieRepository.merge(cookie.domain(), cookie.expiresAt(), cookie.hostOnly(), cookie.httpOnly(), cookie.name(), cookie.path(), cookie.secure(), username, cookie.value());
            else
                cookieRepository.save(new ApiClientCookie(username, cookie.domain(), cookie.expiresAt(), cookie.hostOnly(), cookie.httpOnly(), cookie.name(), cookie.path(), cookie.secure(), cookie.value()));
        });
    }
}
