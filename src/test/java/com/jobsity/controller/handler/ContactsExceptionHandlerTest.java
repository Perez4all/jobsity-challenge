package com.jobsity.controller.handler;

import com.jobsity.controller.dto.Error;
import com.jobsity.exception.KenectClientException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ContactsExceptionHandlerTest {

    private ContactsExceptionHandler contactsExceptionHandler;

    @BeforeEach
    public void setup(){
        contactsExceptionHandler = new ContactsExceptionHandler();
    }

    @Test
    void testHandleThirdPartyCallException(){
        String noResultsFound = "No results found";
        KenectClientException kenectClientException = new KenectClientException(noResultsFound);
        Mono<ResponseEntity<Error>> responseEntityMono = contactsExceptionHandler.handleThirdPartyCallException(kenectClientException);
        StepVerifier.create(responseEntityMono)
                .assertNext(entity -> {
                    Error body = entity.getBody();
                    Assertions.assertEquals(body.getErrorMessage(), noResultsFound);
                    Assertions.assertEquals(entity.getStatusCode(), HttpStatus.NOT_FOUND);
                })
                .verifyComplete();

    }
}
