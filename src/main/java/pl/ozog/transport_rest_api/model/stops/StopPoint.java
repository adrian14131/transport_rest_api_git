package pl.ozog.transport_rest_api.model.stops;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class StopPoint {

    private String id;
    private String name;
    private String label;
    private long latitude;
    private long longitude;
    private String shortId;
    private String category;
    private boolean onDemand;

    public StopPoint(JSONObject stopPoint){
        List<String> list = Arrays.asList("id","label","name","stopPoint","latitude","longitude","onDemand","category");

        if(list.stream().allMatch(stopPoint::containsKey)){
            this.id = (String) stopPoint.get("id");
            this.label = (String) stopPoint.get("label");
            this.name = (String) stopPoint.get("name");
            this.shortId = (String) stopPoint.get("stopPoint");
            this.latitude = (long) stopPoint.get("latitude");
            this.longitude = (long) stopPoint.get("longitude");
            this.onDemand = (boolean) stopPoint.get("onDemand");
            this.category = (String) stopPoint.get("category");
        }
        else throw new IllegalArgumentException("Required keys not exists in JSONObject");
    }

}
