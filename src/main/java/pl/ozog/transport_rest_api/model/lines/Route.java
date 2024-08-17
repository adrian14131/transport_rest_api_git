package pl.ozog.transport_rest_api.model.lines;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.ozog.transport_rest_api.model.stops.StopPoint;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Route {

    private int routeNumber;
    private ArrayList<String> stopsShortIds;
    private String directionId;
    private LinkedHashMap<String, String> stopsUrls;

    public Route(int routeNumber, ArrayList<String> stopsShortIds, LinkedHashMap<String, String> stopsUrls) {
        this.routeNumber = routeNumber;
        this.stopsShortIds = stopsShortIds;
        if(!stopsShortIds.isEmpty())
            this.directionId = stopsShortIds.get(stopsShortIds.size()-1);
        this.stopsUrls = stopsUrls;
    }
    public Route(int routeNumber, ArrayList<String> stopsShortIds) {
        this.routeNumber = routeNumber;
        this.stopsShortIds = stopsShortIds;
        if(!stopsShortIds.isEmpty())
            this.directionId = stopsShortIds.get(stopsShortIds.size()-1);
        this.stopsUrls = new LinkedHashMap<>();
    }
    public Route(int routeNumber) {
        this.routeNumber = routeNumber;
        this.stopsShortIds = new ArrayList<>();
        this.directionId = "";
        this.stopsUrls = new LinkedHashMap<>();
    }

    public void addStopPoint(StopPoint stopPoint){
        stopsShortIds.add(stopPoint.getShortId());
    }



}
