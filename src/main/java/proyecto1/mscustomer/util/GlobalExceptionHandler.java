package proyecto1.mscustomer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Manejo de excepciones ResponseStatusException
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
    logger.error("Error capturado: {}", ex.getMessage());

    Map<String, String> errorResponse = Map.of(
            "error", ex.getReason() != null ? ex.getReason() : "Error desconocido",
            "status", String.valueOf(ex.getRawStatusCode())
    );

    return ResponseEntity.status(ex.getRawStatusCode()).body(errorResponse);
  }

  /**
   * Manejo de excepciones generales (otros errores inesperados)
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
    logger.error("Error inesperado: {}", ex.getMessage());

    Map<String, String> errorResponse = Map.of(
            "error", "Error interno del servidor",
            "status", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

}
