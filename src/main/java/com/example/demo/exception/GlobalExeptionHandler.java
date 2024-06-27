package com.example.demo.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;

@RestControllerAdvice
public class GlobalExeptionHandler {

//    Xử lý các exception
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptionErrorResponse(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        String message = e.getMessage();
        if (e instanceof MethodArgumentNotValidException ){
            //        Lấy ký tự cuối cùng
            int start = message.lastIndexOf("[");
            int end = message.lastIndexOf("]");
            errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorResponse.setMessage(message.substring(start + 1, end - 1));
        }else if(e instanceof ConstraintViolationException){
            errorResponse.setError("Path valid able Invalid");
            errorResponse.setMessage(message.substring(message.indexOf(" ")+1));
        }
        errorResponse.setMessage(message);

        return errorResponse;
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerExceptionErrorResponse(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        if(e instanceof  MethodArgumentTypeMismatchException){
            errorResponse.setMessage("Failed to convert value of type");
        }
        return errorResponse;
    }
}
