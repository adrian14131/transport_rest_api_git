package pl.ozog.transport_rest_api.model.lines;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Getter
@Setter
@EqualsAndHashCode
public class Line {

    private String line;
    private ArrayList<Route> routes;
    private ArrayList<String> routesUrls;

    public Line(String line){
        this.line = line;
        this.routes = new ArrayList<>();
        this.routesUrls = new ArrayList<>();
    }

    public void addRoute(ArrayList<String> stopsShortIds){
        routes.add(new Route(routes.size()+1,stopsShortIds));
    }
    public void addRoute(ArrayList<String> stopsShortIds, LinkedHashMap<String, String> stopsUrls){
        routes.add(new Route(routes.size()+1, stopsShortIds, stopsUrls));
    }
    public void addRouteUrl(String url){
        routesUrls.add(url);
    }

}
