package pl.ozog.transport_rest_api.generator.loaders.mpk;

import lombok.Getter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.ozog.transport_rest_api.generator.Generator;
import pl.ozog.transport_rest_api.generator.connections.Connections;
import pl.ozog.transport_rest_api.generator.loaders.ttss.StopsLoader;
import pl.ozog.transport_rest_api.model.lines.Line;
import pl.ozog.transport_rest_api.model.stops.StopGroup;
import pl.ozog.transport_rest_api.model.stops.StopPoint;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class LineLoader {

    private ArrayList<Line> lines;
    private String url;
    private ArrayList<String> baned;
    public LineLoader(String url) {
        this.url = url;
        this.lines = new ArrayList<>();
        this.baned = new ArrayList<>();
        for(int i=0; i<100; i++){
            baned.add(String.valueOf(i));
        }
    }

    public LineLoader(String url, ArrayList<StopGroup> stopGroups){
        this(url);
        if(stopGroups!=null) loadLines(url, stopGroups);
    }

    private void generator(ArrayList<StopGroup> stopGroups){
        loadLines(url, stopGroups);
    }

    private Map<String, String> loadLinesUrls(String url){
        Map<String, String> lineUrls = new LinkedHashMap<>();
        Document mainDoc = Connections.download(url, "lines");
        Elements lineGroups = mainDoc.select(".linia_table_left > a");
        for (Element lineHref: lineGroups){
            String lineUrl = lineHref.attr("href");
            String line = lineHref.text();
            if(lineUrl!=null && line!=null){
                if(!lineUrl.isEmpty() && !line.isEmpty()){
                    lineUrls.put(line, lineUrl);
                }
            }
        }
        return lineUrls;
    }

    private String[] getStringFilterParts(String url){
        String action = "akcja=index";
        String[] parts = url.split("&");
        return new String[]{(parts[0] + "&" + action + "&"), (parts[1] + "__")};
    }

    private void prepareLinesList(String url){
        int i=0;
        Map<String, String> linesUrls = loadLinesUrls(url);
        for(Map.Entry<String, String> entry: linesUrls.entrySet()){

            if(baned.contains(entry.getKey())) continue;
            Line line = new Line(entry.getKey());
            String[] filterParts = getStringFilterParts(entry.getValue());
            Document lineDoc = Connections.download(url+entry.getValue(), "lines");
            Elements elements = lineDoc.select("td > a");

            for(Element element: elements){
                if(element.attr("href").contains(filterParts[0]) && element.attr("href").contains(filterParts[1])){
                    line.addRouteUrl(element.attr("href"));
                }

            }
            Generator.percent = Generator.calculatePercentage(i+1, linesUrls.size(), 0, 50);

            lines.add(line);
            Generator.numberOfLines++;
            i++;

        }
    }
    private Element getTable(Elements elements, String filter){
        for(Element element: elements){
            if(element.attr("href").contains(filter+"__")){
                return  element.parent().parent().parent();
            }
        }
        return null;
    }

    private LinkedHashMap<String, String> prepareStopIdList(String mainUrl, String routeUrl, ArrayList<StopGroup> stopGroups){
        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        Document routeDoc = Connections.download(mainUrl+routeUrl,"lines");
        String filter = routeUrl.split("&")[routeUrl.split("&").length-1];
        Element table = getTable(routeDoc.select("td > a"), filter);
        for(Element element: table.getElementsByAttributeValueContaining("style", " text-align: right; ")){
            String name = element.text();
            if(element.text().equals("GRANICA TARYF")){
                continue;
            }
            String url = null;
            for(Element a: element.getElementsByTag("a")){
                if(a.attr("href").contains(filter)){
                    url = a.attr("href");
                    break;
                }
            }
            String id = null;
            if(url!=null) {
                StopPoint stopPoint = StopsLoader.getStopPointByName(stopGroups, name);
                if(stopPoint!=null){
                    id = stopPoint.getShortId();
                }
            }
            if(url==null || id==null){
                StopGroup stopGroup = StopsLoader.getStopGroupByName(stopGroups, name);
                if(stopGroup!=null){
                    id = stopGroup.getShortId();
                }
            }

            if(id!=null){
                result.put(id, url);
            }
        }
        return result;
    }
    private void loadRoutes(String url, Line line, ArrayList<StopGroup> stopGroups, int i, int lines){
        int j=1;
        for(String routeUrl: line.getRoutesUrls()){
            LinkedHashMap<String, String> stopList = prepareStopIdList(url, routeUrl, stopGroups);
            ArrayList<String> keys = new ArrayList<>(stopList.keySet());
            line.addRoute(keys, stopList);
            Generator.numberOfRoutes++;
            Generator.percent = Generator.calculatePercentage(i+((float)j/line.getRoutesUrls().size()), (float) lines, 50, 100);
            j++;

        }

    }
    public void loadLines(String url, ArrayList<StopGroup> stopGroups){

        prepareLinesList(url);
        int i=0;
        for(Line line: lines){
            loadRoutes(url, line, stopGroups, i+1, lines.size());
            i++;
        }
    }
}
