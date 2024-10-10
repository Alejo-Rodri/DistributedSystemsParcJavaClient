package alejandro.services.FileServiceF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;
import com.grpc.Syncronization;

import alejandro.model.CookieManager;
import alejandro.model.FileU;
import alejandro.model.User;
import alejandro.services.UserServiceF.UserService;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class FileService implements IFileService {
      
    private UserService user = new UserService();
    
    public List<FileU> getSharedFiles() throws IOException {
        String apiUrl = "http://conquest3.bucaramanga.upb.edu.co:5000/files/shared";        
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(CookieManager.loadCookies())
                .build()) {
                    HttpGet httpGet = new HttpGet(apiUrl);
                    httpGet.addHeader("Content-Type", "application/json");
                    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                        int statusCode = response.getCode();
                        if (statusCode == 200) {
                            HttpEntity entity = response.getEntity();
                            if (entity != null) {
                                String responseString = EntityUtils.toString(entity);
                                System.out.println("respuesta:"+ responseString);
                                try {
                                    JsonObject jsonObject = new Gson().fromJson(responseString, JsonObject.class);

                                    if (jsonObject.has("data")) {
                                        JsonObject data = jsonObject.getAsJsonObject("data");
                                        if (data.has("files")) {
                                            JsonArray filesArray = data.getAsJsonArray("files");
                                            List<FileU> files = new ArrayList<>();
                                            for (int i = 0; i < filesArray.size(); i++) {
                                                FileU file = new Gson().fromJson(filesArray.get(i), FileU.class);
                                                files.add(file);
                                            }
                                            return files;
                                        }
                                    } 
                                }
                                catch (JsonSyntaxException e) {
                                    System.err.println("Error de sintaxis JSON: " + e.getMessage());
                                }
                            } else {
                                System.out.println("La respuesta no contiene entidad.");
                            }
                        } else {
                            System.out.println("Error en la solicitud. Código de estado: " + statusCode);
                        }
                    } catch (IOException | ParseException e) {
                        System.err.println("Error al procesar la respuesta: " + e.getMessage());
                    }
                }
            return null;
    }

    public void syncFiles() {
        
        String target = "10.154.12.122:50052";
        try {

            ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
            System.out.println("Sync Client");
            Syncronization client = new Syncronization(channel);
            client.sync();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
