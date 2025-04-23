package t1.openSchool.exception;

public class ValidationException extends RuntimeException {
    private final String field;
    private final String message;

    public ValidationException(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}