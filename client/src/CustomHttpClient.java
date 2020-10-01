import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CustomHttpClient {
    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private final EncryptionService encryptionService = new EncryptionService();
    private Charset defaultCharset = StandardCharsets.UTF_8;
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

    /*public String getSessionKey(String publicKey) throws CryptException {
        RSA rsaChifer = new RSA(73, 79);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(filename))
                .uri(URI.create(url + "/delete"))
                .build();
    }*/

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

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        byte[] bytesEncrypted = response.body();
        return encryptionService.decrypt(bytesEncrypted);
    }

    public boolean deleteText(String filename) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(filename))
                .uri(URI.create(url + "/delete"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body().equals("deleted");
    }

    public boolean createText(String filename) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(filename))
                .uri(URI.create(url + "/create"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body().equals("created");
    }

    public boolean saveText(String filename, String text) throws IOException, InterruptedException {
        String data = filename + '\n' + text;
        byte[] dataEncrypted = encryptionService.encrypt(data);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(dataEncrypted))
                .uri(URI.create(url + "/save"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body().equals("saved");
    }

    public void getSessionKey() throws IOException, InterruptedException {
        byte[] keyRSA = this.encryptionService.getPublicKeyRSA();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(keyRSA))
                .uri(URI.create(url + "/key"))
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        this.encryptionService.setSessionKey(this.encryptionService.decryptRSA(response.body()));
    }
}
