package chaoxinghelper.apiclient.exception;

public class CoursesGetException extends Exception {

    public CoursesGetException(String message) {
        super(message);
    }

    public CoursesGetException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoursesGetException(Throwable cause) {
        super(cause);
    }

    public CoursesGetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
