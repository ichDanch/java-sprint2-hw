package model;

import java.time.format.DateTimeFormatter;

public class FormatterStartTime {
    public static DateTimeFormatter getFormatterStartTime() {
        return DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
    }

}
