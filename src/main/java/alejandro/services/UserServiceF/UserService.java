package alejandro.services.UserServiceF;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

import alejandro.model.CookieManager;

import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.Header;

public class UserService implements IUserService {
    public String login(String username, String password) throws IOException {
        String apiUrl = "http://conquest3.bucaramanga.upb.edu.co:5000/auth/login";
        String token = null;

        // Usar el CookieStore compartido
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(CookieManager.getCookieStore()) // Usa el CookieStore compartido
                .build()) {

            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Accept", "application/json");

            // Crear el cuerpo de la solicitud
            String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
            httpPost.setEntity(new StringEntity(jsonInputString, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();

                if (statusCode == 200) {
                    Header[] headers = response.getHeaders("Set-Cookie");
                    for (Header header : headers) {
                        String cookieHeader = header.getValue();
                        List<HttpCookie> cookies = HttpCookie.parse(cookieHeader);
                        for (HttpCookie cookie : cookies) {
                            if ("tkn".equals(cookie.getName())) {
                                token = cookie.getValue();
                                break;
                            }
                        }
                    }

                    EntityUtils.consume(response.getEntity()); // Consumir la entidad
                    System.out.println("Login exitoso. Token: " + token);
                    return token; // Retorna el token
                } else {
                    System.out.println("Error en la solicitud. Código de estado: " + statusCode);
                }
            }
        }
        return null;
    }
}
