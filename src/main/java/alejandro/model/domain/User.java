package alejandro.model.domain;

public class User {
    private final String id;
    private String username;
    private String password;
    private String jwt;
    //private String role;
    //private boolean active;

    public User(String id, String username, String password, String jwt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.jwt = jwt;
    }
    

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJwt() { return jwt; }

    /*
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }*/

    /*
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }*/

    public boolean isNull() { return false; }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
