package chaoxinghelper.apiclient;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 发送超星 API 请求时所用的 OkHTTP 拦截器
 */
@Slf4j
class ChaoxingApiRequestInterceptor implements Interceptor {

    /**
     * Token 参数的 Query Key
     */
    private static final String TOKEN_QUERY_KEY = "token";

    /**
     * DES Key 参数的 Query Key
     */
    private static final String DES_KEY_QUERY_KEY = "DESKey";

    /**
     * 当前时间戳的 Query Key
     */
    private static final String TIMESTAMP_QUERY_KEY = "_time";

    /**
     * 超星 API 的验证参数的 Query 参数名
     */
    private static final String VERIFY_QUERY_KEY = "inf_enc";

    /**
     * 请求超星 API 所需的 Token，为固定值
     */
    private static final String TOKEN = "4faa8662c59590c6f43ae9fe5b002b42";

    /**
     * DES Key，在此处是生成 <code>inc_enc</code> 所需要的字符串的一部分
     */
    private static final String DES_KEY = "Z(AfY@XS";

    /**
     * MD5 生成器
     */
    private static MessageDigest md5Digest;

    static {
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成超星 API 请求所需要的 <code>inc_enc</code> 参数。
     * <p>
     * <p/>超星客户端在请求 API 的时候会附带三个 Query 参数：<code>token</code>、<code>_time</code> 和 <code>inc_enc</code>。
     * 其中 <code>token</code> 参数是固定的值 <code>4faa8662c59590c6f43ae9fe5b002b42</code>。<code>_time</code> 为当前时间
     * 的 UNIX 时间戳。<code>inf_enc</code> 为验证参数。计算验证参数时还需要参数 <code>DESKey</code>，固定为 <code>Z(AfY@XS</code>。
     * 将传入 Query 参数按顺序加上 <code>token</code>、<code>_time</code> 和 <code>DESKey</code>。参数（表示为键值对）的键和值以
     * <code>=</code> 连接，参数之间以 <code>&</code> 连接。所得字符串的 MD5 值即为验证参数 <code>inf_enc</code>。
     *
     * @param queryParameters 传入 API 请求的 Query 参数（未加上 <code>token</code>、<code>_time</code> 和 <code>inf_enc</code> 参数）
     * @param timestamp       当前时间的 UNIX 时间戳
     * @return 验证参数 <code>inc_enc</code>
     */
    private static String generateApiRequestVerifyValue(Map<String, String> queryParameters, long timestamp) throws UnsupportedEncodingException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>(queryParameters);
        map.put(TOKEN_QUERY_KEY, TOKEN);
        map.put(TIMESTAMP_QUERY_KEY, Long.toString(timestamp));
        map.put(DES_KEY_QUERY_KEY, DES_KEY);
        StringBuilder verifyStringBuilder = new StringBuilder();
        int i = 0;
        for (var entry : map.entrySet()) {
            if (i != 0)
                verifyStringBuilder.append('&');
            verifyStringBuilder.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            i++;
        }
        StringBuilder verifyParameterBuilder = new StringBuilder();
        for (var b : md5Digest.digest(verifyStringBuilder.toString().getBytes()))
            verifyParameterBuilder.append(String.format("%02x", b));
        return verifyParameterBuilder.toString();
    }

    public Response intercept(Chain chain) throws IOException {
        long timestamp = System.currentTimeMillis();
        LinkedHashMap<String, String> queryParameters = new LinkedHashMap<>();
        HttpUrl requestUrl = chain.request().url();
        for (int i = 0; i < requestUrl.querySize(); i++)
            queryParameters.put(requestUrl.queryParameterName(i), requestUrl.queryParameterValue(i));
        var newUrl = requestUrl.newBuilder()
                .addQueryParameter(TOKEN_QUERY_KEY, TOKEN)
                .addQueryParameter(TIMESTAMP_QUERY_KEY, Long.toString(timestamp))
                .addQueryParameter(VERIFY_QUERY_KEY, generateApiRequestVerifyValue(queryParameters, timestamp))
                .build();
        log.debug(newUrl.toString());
        return chain.proceed(chain.request().newBuilder().url(newUrl).build());
    }

}
