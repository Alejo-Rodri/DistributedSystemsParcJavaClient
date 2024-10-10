package alejandro.model;

public class userSingleton {
    
    private static User instance;

    private userSingleton() {
    
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    
    public static String getUsername() {
        return getInstance().getUsername();
    }

    public static void setUsername(String username) {
        getInstance().setUsername(username);
    }

    public static String getPassword() {
        return getInstance().getPassword();
    }

    public static void setPassword(String password) {
        getInstance().setPassword(password);
    }

    public static String getToken() {
        return getInstance().getToken();
    }

    public static void setToken(String token) {
        getInstance().setToken(token);
    }

    public static void resetInstance() {
        instance = null;
    }
}
