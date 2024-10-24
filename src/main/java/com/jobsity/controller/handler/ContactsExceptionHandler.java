package com.jobsity.controller.handler;

import com.jobsity.exception.KenectClientException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;
import com.jobsity.controller.dto.Error;

@ControllerAdvice
public class ContactsExceptionHandler {

    @ExceptionHandler(KenectClientException.class)
    public Mono<ResponseEntity<Error>> handleThirdPartyCallException(Exception ex){
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Error.builder().errorMessage(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value()).build()));
    }
}
