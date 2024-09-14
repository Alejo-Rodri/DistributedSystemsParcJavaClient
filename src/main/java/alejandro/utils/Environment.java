package alejandro.utils;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Environment {
    private static Environment instance;
    private final HashMap<String, String> variables;

    private Environment() {
        variables = new HashMap<>();
        loadVariables(getPathProperties());
    }
    private String getPathProperties() {
        return "src/main/resources/var.env";
        //return "./var.env";
    }

    private void loadVariables(String path) {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            properties.load(fileInputStream);
            variables.put("CLIENT_IP", properties.getProperty("CLIENT_IP"));
            variables.put("GO_IP", properties.getProperty("GO_IP"));
            variables.put("GO_PORT", properties.getProperty("GO_PORT"));
            variables.put("GRPC_IP", properties.getProperty("GRPC_IP"));
            variables.put("GRPC_PORT", properties.getProperty("GRPC_PORT"));
        } catch (Exception exception) {
            Logs.logWARNING(this.getClass().getName(), "Singleton failed:c", exception);
        }
    }

    public static Environment getInstance() {
        if (Environment.instance == null) Environment.instance = new Environment();
        return Environment.instance;
    }

    public Map<String, String> getVariables() { return variables; }
}
