package model;

import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DurationAdapter extends TypeAdapter<Duration> {
    //    private static final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("dd--MM--yyyy");
//    private static final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter formatterStartTime = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        jsonWriter.value(duration.toMinutes());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        return Duration.ofMinutes(jsonReader.nextLong());
    }
}