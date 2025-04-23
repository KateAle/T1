package t1.openSchool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ex.getField(), ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTaskNotFoundException(TaskNotFoundException ex, WebRequest request) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
