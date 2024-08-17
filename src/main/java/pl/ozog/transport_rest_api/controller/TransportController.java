package pl.ozog.transport_rest_api.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pl.ozog.transport_rest_api.generator.Generator;
import pl.ozog.transport_rest_api.generator.async.AsyncGenerator;

import java.io.IOException;



@RestController
@RequestMapping("/transport/")
public class TransportController {

    @Autowired
    AsyncGenerator asyncGenerator;

    String transportJsonPath = "json/transport.json";
    String generatorJsonPath = "json/generator.json";
    String measureJsonPath = "json/measures.json";
    @GetMapping("generate")
    public ResponseEntity<JSONObject> startGenerateProcess(HttpServletResponse response) {
        if(Generator.isFileExists(transportJsonPath)){
            if (!Generator.loaded){
                Generator.transport = Generator.deserializeFromJson(Generator.readJsonFromFile(transportJsonPath));
                if(!Generator.transport.getLines().isEmpty() && !Generator.transport.getStops().isEmpty()){
                    Generator.isReady = true;
                }
            }

            Generator.loaded = true;
            if(Generator.isDataReady() && !Generator.canGenerate()){
                Generator.percent = 100;
                return new ResponseEntity<>(Generator.serializeToJson(Generator.transport), HttpStatus.OK);
            }
        }
        if(Generator.canGenerate() && !Generator.processing){
            Generator.changeReadyStatus(generatorJsonPath,"NOT READY");
            Generator.percent = 0;

            asyncGenerator.startGeneratingProcess();

            Generator.savePercentageJSON(generatorJsonPath, Generator.percent);
            return new ResponseEntity<>(Generator.readJsonFromFile(generatorJsonPath), HttpStatus.ACCEPTED);
        }
        else if(Generator.processing){
            Generator.savePercentageJSON(generatorJsonPath, Generator.percent);
            return new ResponseEntity<>(Generator.readJsonFromFile(generatorJsonPath), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(Generator.readJsonFromFile(generatorJsonPath), HttpStatus.BAD_REQUEST);
    }
    @GetMapping("status")
    public JSONObject status(){
        Generator.savePercentageJSON(generatorJsonPath, Generator.percent);
        return Generator.readJsonFromFile(generatorJsonPath);
    }
    @GetMapping("get")
    public ResponseEntity<JSONObject> getTransport(HttpServletResponse response){
        if(Generator.isFileExists(transportJsonPath)){
            if (!Generator.loaded){
                Generator.transport = Generator.deserializeFromJson(Generator.readJsonFromFile(transportJsonPath));
                if(!Generator.transport.getLines().isEmpty() && !Generator.transport.getStops().isEmpty()){
                    Generator.isReady = true;
                }
            }

            Generator.loaded = true;
        }
        if(Generator.isDataReady() && !Generator.canGenerate()){
            return new ResponseEntity<>(Generator.serializeToJson(Generator.transport), HttpStatus.OK);
        }
        else{
            if(Generator.loaded && !Generator.processing){
                return new ResponseEntity<>(Generator.serializeToJson(Generator.transport), HttpStatus.OK);
            }
            try {
                response.sendRedirect("generate");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new ResponseEntity<>(Generator.readJsonFromFile(generatorJsonPath), HttpStatus.NO_CONTENT);
    }
    @GetMapping("measures")
    public JSONObject getMeasures(){
        if(Generator.isFileExists(measureJsonPath))
            return Generator.readJsonFromFile(measureJsonPath);
        else
            return new JSONObject();
    }

}
