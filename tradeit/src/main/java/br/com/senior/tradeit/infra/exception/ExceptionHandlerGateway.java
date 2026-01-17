package br.com.senior.tradeit.infra.exception;


import br.com.senior.tradeit.infra.exception.dto.ErrorMessageDTO;
import br.com.senior.tradeit.infra.exception.dto.Field;
import br.com.senior.tradeit.infra.exception.dto.FieldErrorsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerGateway {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.notFound()
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessageDTO> handleBadRequestError(BadRequestException ex) {
        ErrorMessageDTO error = new ErrorMessageDTO(ex.getMessage());
        return ResponseEntity.badRequest()
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FieldErrorsDTO> handleBadRequestError(MethodArgumentNotValidException ex) {
        List<Field> fieldErrors = ex.getFieldErrors()
                .stream()
                .map(fieldError -> {
                    String field = fieldError.getField();
                    String message = fieldError.getDefaultMessage();
                    return new Field(field, message);
                })
                .toList();
        var error = new FieldErrorsDTO(fieldErrors);
        return ResponseEntity.badRequest()
                .body(error);
    }

    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<ErrorMessageDTO> handleInternalServerError(ServerErrorException ex) {
        var error = new ErrorMessageDTO(ex.getMessage());
        return ResponseEntity.internalServerError()
                .body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException exc) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                .build();
    }

}
