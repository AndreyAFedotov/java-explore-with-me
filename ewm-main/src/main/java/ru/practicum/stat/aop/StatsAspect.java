package ru.practicum.stat.aop;

import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.practicum.stat.RemoteStatService;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@AllArgsConstructor
public class StatsAspect {
    private RemoteStatService statService;
    private HttpServletRequest request;

    @AfterReturning(pointcut = "@annotation(SaveStatistic)")
    public void logStat() {
        statService.createHit(request);
    }

}
