package pl.ozog.transport_rest_api.generator.exceptions;

public class EmptyResponseException extends Exception{
    public EmptyResponseException(String message){
        super(message);
    }
}
