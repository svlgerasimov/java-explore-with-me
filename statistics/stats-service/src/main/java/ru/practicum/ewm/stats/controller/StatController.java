package ru.practicum.ewm.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.StatDtoIn;
import ru.practicum.ewm.stats.dto.StatDtoOut;
import ru.practicum.ewm.stats.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    public ResponseEntity<Void> saveHit(@RequestBody @Valid StatDtoIn statDtoIn) {
        statService.save(statDtoIn);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatDtoOut>> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(defaultValue = "") List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
            ) {
        return ResponseEntity.ok()
                .body(statService.get(start, end, uris, unique));
    }
}
