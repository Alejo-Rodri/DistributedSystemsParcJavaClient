package alejandro.model;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

public class CookieManager {

    private static final CookieStore cookieStore = new BasicCookieStore();
    private static final String COOKIE_FILE_PATH = "cookies.txt"; // Ruta del archivo para guardar las cookies

    public static CookieStore getCookieStore() {
        return cookieStore;
    }

    public static void saveCookies() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COOKIE_FILE_PATH))) {
            List<org.apache.hc.client5.http.cookie.Cookie> cookies = cookieStore.getCookies();
            for (org.apache.hc.client5.http.cookie.Cookie cookie : cookies) {
                writer.write(cookie.getName() + "=" + cookie.getValue());
                writer.newLine();
            }
            System.out.println("Cookies guardadas en " + COOKIE_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error al guardar las cookies: " + e.getMessage());
        }
    }

    public static CookieStore loadCookies() {
    CookieStore cookieStore = new BasicCookieStore();
    try (BufferedReader reader = new BufferedReader(new FileReader("cookies.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] cookieParts = line.split(";");
            for (String part : cookieParts) {
                String[] keyValue = part.split("=");
                if (keyValue.length == 2) {
                    String name = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    BasicClientCookie cookie = new BasicClientCookie(name, value);
                    cookie.setDomain("conquest3.bucaramanga.upb.edu.co"); // Asegúrate de establecer el dominio
                    cookie.setPath("/"); // Asegúrate de establecer el camino
                    cookieStore.addCookie(cookie);
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error al cargar las cookies: " + e.getMessage());
    }
    return cookieStore;
}

}
