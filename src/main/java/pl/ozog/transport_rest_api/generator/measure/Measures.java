package pl.ozog.transport_rest_api.generator.measure;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Measures {
    ArrayList<Measure> measures = new ArrayList<>();

    public void addMeasures(Measure measure){
        measures.add(measure);
    }
}
