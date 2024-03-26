package TestCreator.openAI;

import TestCreator.questions.MultipleChoice;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

//TODO: Generate multiple choice answers with OpenAI's GPT-3 API
public class OpenAIChatGPT {
    private static final String API_KEY = "your-api-key-here";
    private static final String ENDPOINT = "https://api.openai.com/v1/engines/davinci-codex/completions";

    public static void generateMultipleChoiceAnswers(MultipleChoice question, int numChoices) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String prompt = """
            Generate 1 correct answer and %s incorrect answers. Keep the answers brief. Give the correct answer first.
            Surround the correct answer with asterisks.
            Use the following prompt to generate your answers:
            %s
        """.formatted(numChoices - 1, question.getQuestionText());

        String requestBody = STR."{\"prompt\": \"\{prompt}\", \"max_tokens\": 100}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Authorization", STR."Bearer \{API_KEY}")
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    }
}
