package services;

import com.google.gson.Gson;
import entity.Todo;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class TodoService {

    private String baseUrl;
    private String todoEndpoint;
    private HttpClient httpClient;
    private Gson gson;

    public TodoService() {
        try {
            this.loadAPIProperties();
            this.setUpHttpClient();
        } catch (ConfigurationException e) {
            // we can add different exceptions and do something else for each of them
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTodo(Todo todo) throws Exception {
        // convert todo object to json data type
        String requestBody = this.gson.toJson(todo);

        // create http request using httprequest builder
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(todoEndpoint)) // specify the address to send the request
                .timeout(Duration.ofSeconds(30)) // how long should it wait for the response from address / server / api
                .header("Content-Type", "application/json") // what configuration / settings are required from server are provided in header
                //  .header("accept", "application/text")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)) // the type of http request is specified e g post, get, delete
                .build(); // the final copy of the request is compiled and ready for sending
        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Todo createdTodoItem = gson.fromJson(response.body(), Todo.class); // we try to extract th response json

        if (createdTodoItem == null || createdTodoItem.get_id() == null) { // we check if the response was converted properly back to java object, this could also mean that everythin was ok and aPi returned
            throw new Exception("Failed to create todo item" + response.statusCode());
        }

        if (response.statusCode() != 201) { // we csn also check the status code
            throw new Exception("Request failed, API respond with code: " + response.statusCode());
        }
    }

    public List<Todo> getAllTodoItems() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(this.todoEndpoint))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Request failed, API respond with code: " + response.statusCode());
        }

        Todo[] todoList = gson.fromJson(response.body(), Todo[].class);
        return Arrays.asList(todoList);
    }

    private void setUpHttpClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    private void loadAPIProperties() throws Exception {
        final PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfiguration.load("application.properties");
        this.baseUrl = propertiesConfiguration.getString("api.baseUrl");
        this.todoEndpoint = this.baseUrl + propertiesConfiguration.getString("api.todoEndpoint");

    }

    public Todo getTodoItem(String todoId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(this.todoEndpoint + "/" + todoId))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Unable to find item with id. Error code: " + response.statusCode());
        }

        return gson.fromJson(response.body(), Todo.class);
    }

    public void deleteTodoItem(String todoId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(this.todoEndpoint + "/" + todoId))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Unable to find item with id. Error code: " + response.statusCode());
        }
    }

    public Todo updateTodoItem(Todo todo, String todoID) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(this.todoEndpoint + "/" + todoID))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(this.gson.toJson(todo)))
                .build();
        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Unable to find item with id. Error code: " + response.statusCode());
        }

        return gson.fromJson(response.body(), Todo.class);
    }
}
