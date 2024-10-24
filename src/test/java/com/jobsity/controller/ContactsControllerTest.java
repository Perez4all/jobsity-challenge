package com.jobsity.controller;

import com.jobsity.controller.dto.Contact;
import com.jobsity.service.ContactsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import java.util.Date;

@ExtendWith(SpringExtension.class)
@WebFluxTest(ContactsController.class)
public class ContactsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ContactsService contactsService;

    @Test
    void testGetAllContacts() throws Exception {
        Contact contact = new Contact();
        Date createdAt = new Date();
        contact.setId(1);
        contact.setEmail("andres96666@gmail.com");
        contact.setSource("KENECT_LABS");
        contact.setCreated_at(createdAt);
        contact.setUpdated_at(createdAt);

        Mockito.doReturn(Flux.just(contact)).when(contactsService).getContacts(null);

        webTestClient.get().uri("/contacts")
                        .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$[0].id")
                .isEqualTo(1)
                .jsonPath("$[0].email")
                .isEqualTo("andres96666@gmail.com");
    }

    @Test
    void testGetAllContactsByPage(){
        Contact contact = new Contact();
        Date createdAt = new Date();
        contact.setId(1);
        contact.setEmail("andres96666@gmail.com");
        contact.setSource("KENECT_LABS");
        contact.setCreated_at(createdAt);
        contact.setUpdated_at(createdAt);

        Mockito.doReturn(Flux.just(contact)).when(contactsService).getContacts(1);

        webTestClient.get().uri(builder -> builder.path("/contacts")
                        .queryParam("page", 1).build())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$[0].id")
                .isEqualTo(1)
                .jsonPath("$[0].email")
                .isEqualTo("andres96666@gmail.com");
    }

}
