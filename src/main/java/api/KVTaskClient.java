package api;

import exception.KVTaskClientException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final URI uri;
    private String token;
    private final HttpClient httpClient;

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
            } else if (response.statusCode() == 404) {
                return null;
            } else {
                throw new KVTaskClientException("Во время выполнения запроса возникла ошибка: сервер вернул код состояния отличное: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new KVTaskClientException("Во время выполнения запроса возникла ошибка: " + e.getMessage());
        }
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
