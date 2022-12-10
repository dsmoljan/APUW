package hr.fer.apuw.lab1.exception;

public class RequestDeniedException extends RuntimeException {

    public RequestDeniedException() {
        super();
    }

    public RequestDeniedException(String message) {
        super(message);
    }

    public RequestDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

}
