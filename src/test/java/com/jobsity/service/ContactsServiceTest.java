package com.jobsity.service;

import com.jobsity.client.KenectLabsClient;
import com.jobsity.client.dto.KenectContact;
import com.jobsity.controller.dto.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ContactsServiceTest {

    private ContactsService contactsService;

    @Mock
    private KenectLabsClient kenectLabsClient;

    @BeforeEach
    public void setup(){
        contactsService = new ContactsServiceImpl(kenectLabsClient);
        ReflectionTestUtils.setField(contactsService, "KENECT_LABS_SOURCE", "KENECT_LABS");
    }

    @Test
    void testGetAllContacts(){

        KenectContact contact = new KenectContact();
        Date createdAt = new Date();
        contact.setId(1);
        contact.setEmail("andres96666@gmail.com");
        contact.setCreated_at(createdAt);
        contact.setUpdated_at(createdAt);

        doReturn(Flux.just(contact)).when(kenectLabsClient).getAllContacts();

        Flux<Contact> contacts = contactsService.getContacts(null);

        StepVerifier.create(contacts)
                .assertNext(contactResult -> {
                    assertEquals(1, contactResult.getId());
                    assertEquals("andres96666@gmail.com", contactResult.getEmail());
                    assertEquals(createdAt, contactResult.getUpdated_at());
                    assertEquals(createdAt, contactResult.getCreated_at());
                    assertEquals("KENECT_LABS", contactResult.getSource());
                }).verifyComplete();

        verify(kenectLabsClient, times(1)).getAllContacts();
    }

    @Test
    void testGetContactsByPage(){
        KenectContact contact = new KenectContact();
        Date createdAt = new Date();
        contact.setId(1);
        contact.setEmail("andres96666@gmail.com");
        contact.setCreated_at(createdAt);
        contact.setUpdated_at(createdAt);

        doReturn(Flux.just(contact)).when(kenectLabsClient).getContactsByPage(1);

        Flux<Contact> contacts = contactsService.getContacts(1);

        StepVerifier.create(contacts)
                .assertNext(contactResult -> {
                    assertEquals(1, contactResult.getId());
                    assertEquals("andres96666@gmail.com", contactResult.getEmail());
                    assertEquals(createdAt, contactResult.getUpdated_at());
                    assertEquals(createdAt, contactResult.getCreated_at());
                    assertEquals("KENECT_LABS", contactResult.getSource());
                }).verifyComplete();

        verify(kenectLabsClient, times(1)).getContactsByPage(1);
    }
}
