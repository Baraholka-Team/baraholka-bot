package baraholkateam.exception;

public class BaraholkaBotException extends Exception {

    public BaraholkaBotException() {
        super();
    }

    public BaraholkaBotException(String message) {
        super(message);
    }

    public BaraholkaBotException(String message, Object... parameters) {
        super(message.formatted(parameters));
    }

    public BaraholkaBotException(Throwable cause, String message) {
        super(message, cause);
    }

    public BaraholkaBotException(Throwable cause, String message, Object... parameters) {
        super(message.formatted(parameters), cause);
    }

}
