package pl.ozog.transport_rest_api.generator.measure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Measure {
    LocalDateTime time;
    Long executionTime;
    long numberOfStopPoints =0, numberOfStopGroups = 0, numberOfLines = 0, numberOfRoutes = 0, numberOfLoadingPages = 0, downloadSize = 0, numberOfRequests = 0;
}
