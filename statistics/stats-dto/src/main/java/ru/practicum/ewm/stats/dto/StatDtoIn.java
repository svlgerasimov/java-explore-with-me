package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Value
@Builder
public class StatDtoIn {
    @NotBlank
    String app;
    @NotBlank
    String uri;
    @Pattern(regexp = "^(((25[0-4])|((2[0-4]|1\\d|[1-9])?\\d))\\.?\\b){4}$")
    String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime timestamp;
}
