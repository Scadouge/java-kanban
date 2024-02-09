package api;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final URI uri;
    private String token;
    HttpClient httpClient;

    public KVTaskClient(URI uri) {
        httpClient = HttpClient.newHttpClient();
        this.uri = uri;
        register();
    }

    private String sendRequest(HttpRequest request) {
        try {
            HttpResponse.BodyHandler<String> bodyHandlers = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = httpClient.send(request, bodyHandlers);
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException ignored) {
            System.out.println("s");
        }
        return null;
    }

    private void register() {
        URI requestUrl = URI.create(uri + "/register");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(requestUrl).build();
        token = sendRequest(request);
    }

    public void put(String key, String json) {
        URI requestUrl = URI.create(uri + "/save/" + key + "?API_TOKEN=" + token);
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(bodyPublisher).uri(requestUrl).build();
        sendRequest(request);
    }

    public String load(String key) {
        URI requestUrl = URI.create(uri + "/load/" + key + "?API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(requestUrl).build();
        return sendRequest(request);
    }
}
