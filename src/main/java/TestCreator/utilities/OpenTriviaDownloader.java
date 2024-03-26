package TestCreator.utilities;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class OpenTriviaDownloader {

    public List<TDBQuestion> downloadQuestions(String categoryString, int amount) throws IOException, InterruptedException {
        TDBQuestion.Categories category = TDBQuestion.Categories.valueOf(categoryString.toUpperCase().replace(" ", "_"));
        return downloadQuestions(category, amount);
    }

    public List<TDBQuestion> downloadQuestions(TDBQuestion.Categories category, int amount) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(STR."https://opentdb.com/api.php?amount=\{amount}&category=\{category.getId()}&type=multiple"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        OpenTDBResponse openTDBResponse = gson.fromJson(response.body(), OpenTDBResponse.class);

        return openTDBResponse.results;
    }

    public static class OpenTDBResponse {
        public final List<TDBQuestion> results = new ArrayList<>();
    }
}
