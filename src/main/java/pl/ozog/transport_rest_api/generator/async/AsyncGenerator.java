package pl.ozog.transport_rest_api.generator.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.ozog.transport_rest_api.generator.Generator;

@Service
public class AsyncGenerator {
    @Async
    public void startGeneratingProcess(){
        Generator.startProcess();
    }
}
