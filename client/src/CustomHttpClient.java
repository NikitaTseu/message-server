import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CustomHttpClient {
    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private String url;

    public boolean testConnection(String ip){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://" + ip + "/test"))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.body().equals("hello")){
                this.url = "http://" + ip;
                return true;
            } else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public String getFilenames() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/filenames"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String loadText(String filename) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(filename))
                .uri(URI.create(url + "/load"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public boolean deleteText(String filename) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(filename))
                .uri(URI.create(url + "/delete"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.body().equals("deleted")){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean createText(String filename) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(filename))
                .uri(URI.create(url + "/create"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.body().equals("created")){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean saveText(String filename, String text) throws IOException, InterruptedException {
        String data = filename + '\n' + text;
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .uri(URI.create(url + "/save"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.body().equals("saved")){
            return true;
        }
        else{
            return false;
        }
    }
}
