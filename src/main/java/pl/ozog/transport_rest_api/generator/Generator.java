package pl.ozog.transport_rest_api.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.time.StopWatch;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pl.ozog.transport_rest_api.adapters.GsonLocalDateDeserializer;
import pl.ozog.transport_rest_api.adapters.GsonLocalDateSerializer;
import pl.ozog.transport_rest_api.adapters.GsonLocalDateTimeDeserializer;
import pl.ozog.transport_rest_api.adapters.GsonLocalDateTimeSerializer;
import pl.ozog.transport_rest_api.generator.loaders.mpk.LineLoader;
import pl.ozog.transport_rest_api.generator.loaders.ttss.StopsLoader;
import pl.ozog.transport_rest_api.generator.loaders.ttss.TTSSApiLoader;
import pl.ozog.transport_rest_api.generator.measure.Measure;
import pl.ozog.transport_rest_api.generator.measure.Measures;
import pl.ozog.transport_rest_api.model.Transport;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Generator {

    public static boolean DEBUG = true;
    public static long downloadedSize = 0;
    public static double percent;
    public static double maxPercent = 100;
    public static boolean isReady = false;
    public static boolean loaded = false;
    public static boolean processing = false;
    public static boolean generateEveryRequest = false;
    public static TTSSApiLoader apiLoader = new TTSSApiLoader("/geoserviceDispatcher/services/stopinfo/stopPoints?left=-648000000&bottom=-324000000&right=648000000&top=324000000");
    public static StopsLoader stopsLoader = new StopsLoader();
    public static LineLoader lineLoader = new LineLoader("https://rozklady.mpk.krakow.pl");
    public static Transport transport = new Transport();
    public static long numberOfStopPoints = 0, numberOfStopGroups = 0, numberOfLines = 0, numberOfRoutes = 0, numberOfLoadingPages = 0, numberOfRequests = 0;
    private static String measureJsonPath = "json/measures.json";
    private static String generatorJsonPath = "json/generator.json";
    private static String transportJsonPath = "json/transport.json";

//    public static void dLog(String message) {
//        if (DEBUG)
//            System.out.println(message);
//    }

    public static void startProcess() {
        Measure measure = new Measure(LocalDateTime.now(), 0L, 0, 0, 0, 0, 0, 0, 0);
        downloadedSize = 0;
        numberOfStopPoints = 0;
        numberOfStopGroups = 0;
        numberOfLines = 0;
        numberOfRoutes = 0;
        numberOfRequests = 0;
        numberOfLoadingPages = 0;
        StopWatch sw = new StopWatch();
        sw.start();
        loaded = false;
        processing = true;
        apiLoader = new TTSSApiLoader("/geoserviceDispatcher/services/stopinfo/stopPoints?left=-648000000&bottom=-324000000&right=648000000&top=324000000");
//        apiLoader.loadJson("tram");
        apiLoader.loadJson("bus");

        stopsLoader = new StopsLoader(apiLoader.getJsons(), true);

        apiLoader.translateJsons();

        stopsLoader.generateStopGroupList(false);

        lineLoader = new LineLoader("https://rozklady.mpk.krakow.pl", stopsLoader.getStops());

        transport = new Transport(LocalDate.now(), lineLoader.getLines(), stopsLoader.getStops());

        loaded = true;
        saveJsonToFile(transportJsonPath, serializeToJson(transport));
        changeReadyStatus(generatorJsonPath, "READY");
        processing = false;
        sw.stop();

        measure.setExecutionTime(sw.getNanoTime());
        measure.setNumberOfLines(numberOfLines);
        measure.setNumberOfRoutes(numberOfRoutes);
        measure.setNumberOfStopGroups(numberOfStopGroups);
        measure.setNumberOfStopPoints(numberOfStopPoints);
        measure.setDownloadSize(downloadedSize);
        measure.setNumberOfRequests(numberOfRequests);
        measure.setNumberOfLoadingPages(numberOfLoadingPages);
        saveMeasure(measureJsonPath, measure);
    }

    public static void saveMeasure(String filePath, Measure measureResult) {

        Measures measures = new Measures();

        if (isFileExists(filePath))
            measures = loadMeasure(filePath);
        measures.addMeasures(measureResult);

        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeSerializer()).serializeNulls().disableHtmlEscaping().create();
        try {
            saveJsonToFile(filePath, (JSONObject) new JSONParser().parse(gson.toJson(measures)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Measures loadMeasure(String filePath) {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeDeserializer()).serializeNulls().disableHtmlEscaping().create();
        return gson.fromJson(readJsonFromFile(filePath).toJSONString(), Measures.class);
    }

    public static boolean canGenerate() {
        return transport.getLastUpdate().isBefore(LocalDate.now()) || generateEveryRequest;
    }

    public static void saveJsonToFile(String filePath, JSONObject jsonObject) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(filePath);
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject readJsonFromFile(String filePath) {
        JSONParser parser = new JSONParser();
        JSONObject result = new JSONObject();
        try {
            FileReader reader = new FileReader(filePath);
            result = (JSONObject) parser.parse(reader);

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Transport deserializeFromJson(JSONObject jsonObject) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().registerTypeAdapter(LocalDate.class, new GsonLocalDateDeserializer()).create();
        return gson.fromJson(jsonObject.toJSONString(), Transport.class);
    }

    public static JSONObject serializeToJson(Transport t) {
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().registerTypeAdapter(LocalDate.class, new GsonLocalDateSerializer()).create();
            return (JSONObject) new JSONParser().parse(gson.toJson(t));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void changeReadyStatus(String generatorJsonPath, String newStatus) {
        JSONObject jsonObject = readJsonFromFile(generatorJsonPath);
        if (jsonObject.containsKey("ready") && jsonObject.containsKey("status")) {
            if (newStatus.equals("READY")) {
                isReady = true;
                jsonObject.replace("ready", true);
            } else if (newStatus.equals("NOT READY")) {
                isReady = false;
                jsonObject.replace("ready", false);
            }
            jsonObject.replace("status", newStatus);
        } else {
            jsonObject.put("ready", newStatus.equals("READY"));
            jsonObject.put("status", newStatus);

        }
        saveJsonToFile(generatorJsonPath, jsonObject);
    }

    public static void savePercentageJSON(String generatorJsonPath, double percent) {
        JSONObject jsonObject = readJsonFromFile(generatorJsonPath);
        if (jsonObject.containsKey("percentage")) {
            jsonObject.replace("percentage", percent);
        } else {
            jsonObject.put("percentage", percent);
        }
        saveJsonToFile(generatorJsonPath, jsonObject);
    }

    public static double calculatePercentage(double value, double from, double minPercentage, double maxPercentage) {
        if (minPercentage > maxPercentage) {
            double temp = minPercentage;
            minPercentage = maxPercentage;
            maxPercentage = temp;
        }
        if (from == 0) return -1;
        double result = ((value / from) * (maxPercentage - minPercentage)) + minPercentage;
        result = Double.valueOf(String.format("%.2f", result).replace(",", "."));
        return result > 100 ? 100 : result;
    }

    public static boolean isDataReady() {
        return isReady;
    }

    public static boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && !file.isDirectory();
    }
}
