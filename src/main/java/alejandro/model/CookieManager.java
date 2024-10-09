package alejandro.model;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;

public class CookieManager {

    private static final CookieStore cookieStore = new BasicCookieStore();

    public static CookieStore getCookieStore() {
        return cookieStore;
    }
}
