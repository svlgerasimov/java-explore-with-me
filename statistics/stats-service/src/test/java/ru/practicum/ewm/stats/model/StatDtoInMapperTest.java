package ru.practicum.ewm.stats.model;

import org.junit.jupiter.api.Test;
import ru.practicum.ewm.stats.dto.StatDtoIn;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class StatDtoInMapperTest {

    private final StatDtoInMapper mapper = new StatDtoInMapperImpl();

    @Test
    void fromDtoTest() {
        String appName = "ewm-main-service";
        AppEntity app = new AppEntity();
        app.setName(appName);
        String ip = "192.163.0.1";
        String uri = "/events/1";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timestamp = LocalDateTime.parse("2022-09-06 11:00:23", dateTimeFormatter);
        StatDtoIn dto = StatDtoIn.builder()
                .ip(ip)
                .uri(uri)
                .timestamp(timestamp)
                .build();

        StatEntity entity = mapper.fromDto(dto, app);

        assertThat(entity).extracting("id", "ip", "uri", "timestamp")
                .containsExactly(null, ip, uri, timestamp);
        assertThat(entity.getApp().getName()).isEqualTo(appName);
    }
}