package in.succinct.beckn.portal.exceptions;

public class JsonSchemaException extends RuntimeException{
    public JsonSchemaException() {
    }

    public JsonSchemaException(String message) {
        super(message);
    }

    public JsonSchemaException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonSchemaException(Throwable cause) {
        super(cause);
    }

    public JsonSchemaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
