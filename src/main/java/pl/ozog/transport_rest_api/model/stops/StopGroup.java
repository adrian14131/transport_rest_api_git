package pl.ozog.transport_rest_api.model.stops;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import java.util.ArrayList;

@Getter
@Setter
public class StopGroup {
    private String name;
    private String shortId;
    private ArrayList<StopPoint> stopPoints;

    public StopGroup(String name, String shortId) {
        this.name = name;
        this.shortId = shortId;
        this.stopPoints = new ArrayList<>();
    }
    public StopGroup(JSONObject stopPoint) {
        if(stopPoint.containsKey("name") && stopPoint.containsKey("shortName")){
            this.name = (String) stopPoint.get("name");
            this.shortId = (String) stopPoint.get("shortName");
            this.stopPoints.add(new StopPoint(stopPoint));
        }
        else throw new IllegalArgumentException("Key 'shortName' or 'name' not exist in JSONObject");
    }
    public StopGroup(String name, JSONObject stopPoint, boolean clean) {
        if(stopPoint.containsKey("shortName")){
            this.name = name;
            this.shortId = (String) stopPoint.get("shortName");
            this.stopPoints = new ArrayList<>();
            if(!clean){
                this.stopPoints.add(new StopPoint(stopPoint));
            }
        }
        else throw new IllegalArgumentException("Key 'shortName' not exist in JSONObject");

    }

    public StopGroup(String name, String shortId, StopPoint stopPoint) {
        this.name = name;
        this.shortId = shortId;
        this.stopPoints = new ArrayList<>();
        if(stopPoint != null){
            this.stopPoints.add(stopPoint);
        }
    }

    public void addStopPoint(JSONObject stopPoint){
        this.stopPoints.add(new StopPoint(stopPoint));
    }
    public void addStopPoint(StopPoint stopPoint){
        this.stopPoints.add(stopPoint);
    }

    public StopPoint getStopPointByShortId(String shortId){
        for(StopPoint stopPoint: stopPoints){
            if(stopPoint.getShortId().equals(shortId)) return stopPoint;
        }
        return null;
    }
    public StopPoint getStopPointByName(String name){
        for(StopPoint stopPoint: stopPoints){
            if(stopPoint.getName().equals(name)) return stopPoint;
        }
        return null;
    }

    public boolean hasShortId(String shortId){
        return this.shortId.equals(shortId);
    }




}
