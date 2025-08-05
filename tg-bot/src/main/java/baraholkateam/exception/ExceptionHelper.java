package baraholkateam.exception;

public class ExceptionHelper {

    public static String getExceptionMessage(Exception e) {
        StringBuilder builder = new StringBuilder(e.getMessage());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            builder.append("\n\t").append(stackTraceElement.toString());
        }
        return builder.toString();
    }

}
