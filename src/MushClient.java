import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class MushClient {
        HttpClient client;
        HttpRequest request;
        HttpResponse<String> response;

    public MushClient() {
        client = HttpClient.newBuilder().build();
    }

    // uses the client to send a request to the pi for sensor data
    public HttpResponse<String> sendRequest() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder().uri(URI.create("http://192.168.50.240:5000/reading")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response;
    }
}
