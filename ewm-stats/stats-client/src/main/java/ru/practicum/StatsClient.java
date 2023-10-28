package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${ewm-stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> createHit(HttpServletRequest req, String app) {
        log.info("{} -> POST Hit request", req.getContextPath());

        String realIp = req.getRemoteAddr().equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : req.getRemoteAddr();

        return post("/hit", new StatsDtoRequest(
                app,
                req.getRequestURI(),
                realIp,
                LocalDateTime.now())
        );
    }

    public ResponseEntity<Object> getStats(String start, String end, Collection<String> uris, Boolean uniq) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", String.join(",", uris),
                "unique", uniq
        );
        log.info("GET Stat request");
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}
