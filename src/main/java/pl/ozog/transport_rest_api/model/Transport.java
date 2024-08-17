package pl.ozog.transport_rest_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.ozog.transport_rest_api.model.lines.Line;
import pl.ozog.transport_rest_api.model.stops.StopGroup;

import java.time.LocalDate;
import java.util.ArrayList;

//@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
public class Transport {
    LocalDate lastUpdate = LocalDate.EPOCH;
    ArrayList<Line> lines;
    ArrayList<StopGroup> stops;

    public Transport(){
        lines = new ArrayList<>();
        stops = new ArrayList<>();
    }
}
