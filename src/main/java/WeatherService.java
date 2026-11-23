import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class WeatherService {
    private static final String API_KEY = "API_KEY_HERE";

    private static final String LATITUDE = "52.37125";
    private static final String LONGITUDE = "4.89388";
    private static final int LIMIT = 7;
    private static final String FORECAST_URL = "https://api.weather.yandex.ru/v2/forecast"
            + "?lat=" + LATITUDE
            + "&lon=" + LONGITUDE
            + "&limit=" + LIMIT;

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(FORECAST_URL))
                .header("X-Yandex-Weather-Key", API_KEY)
                .GET()
                .build();

        String body = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .body();
        System.out.println(body);

        ForecastResponse forecast = new Gson().fromJson(body, ForecastResponse.class);
        System.out.println("Текущая температура: " + forecast.fact.temp);

        double sum = 0.0;
        int days = Math.min(LIMIT, forecast.forecasts.size());

        for (int i = 0; i < days; i++) {
            sum += forecast.forecasts.get(i).parts.day.tempAvg;
        }

        System.out.println("Средняя температура: " + (sum / days));
    }

    private static class ForecastResponse {
        private Fact fact;
        private List<ForecastDay> forecasts;
    }

    private static class Fact {
        private double temp;
    }

    private static class ForecastDay {
        private Parts parts;
    }

    private static class Parts {
        private DayPart day;
    }

    private static class DayPart {
        @SerializedName("temp_avg")
        private double tempAvg;
    }
}
