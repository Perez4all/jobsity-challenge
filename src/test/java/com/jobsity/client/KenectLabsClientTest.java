package com.jobsity.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobsity.client.dto.KenectContact;
import com.jobsity.client.dto.KenectContactResponse;
import com.jobsity.exception.KenectClientException;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

public class KenectLabsClientTest {

    private static final String URL = "dummy-contacts-url";

    private static WebClient webClient;

    public static MockWebServer mockWebServer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private KenectLabsClient kenectLabsClient;

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        webClient = Mockito.spy(WebClient.builder().baseUrl(mockWebServer
                        .url(URL).toString())
                .build());
    }

    @BeforeEach
    public void init(){
        kenectLabsClient = new KenectLabsClientImpl(webClient);
    }

    @Test
    void testGetContactsByPage() throws JsonProcessingException {

        KenectContact kenectContact = new KenectContact();
        kenectContact.setId(1);
        kenectContact.setName("dummy");
        kenectContact.setEmail("andres96666@gmail.com");
        Date createdAt = new Date();
        kenectContact.setCreated_at(createdAt);
        kenectContact.setUpdated_at(createdAt);

        KenectContactResponse kenectContactResponse = new KenectContactResponse();
        kenectContactResponse.setContacts(Collections.singletonList(kenectContact));

        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(kenectContactResponse))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON));

        Flux<KenectContact> contactsByPage = kenectLabsClient.getContactsByPage(1);

        StepVerifier.create(contactsByPage)
                .assertNext(kc -> {
                    Assertions.assertInstanceOf(KenectContact.class, kc);
                    Assertions.assertEquals(kc.getId(), 1);
                    Assertions.assertEquals(kc.getCreated_at(), createdAt);
                    Assertions.assertEquals(kc.getUpdated_at(), createdAt);
                })
                .verifyComplete();
        Mockito.verify(webClient, Mockito.times(1)).get();
    }

    @Test
    void testGetAllContacts() throws JsonProcessingException {

        KenectContact kenectContact = new KenectContact();
        kenectContact.setId(1);
        kenectContact.setName("dummy");
        kenectContact.setEmail("andres96666@gmail.com");
        Date createdAt = new Date();
        kenectContact.setCreated_at(createdAt);
        kenectContact.setUpdated_at(createdAt);

        KenectContact kenectContact2 = new KenectContact();
        kenectContact2.setId(2);
        kenectContact2.setName("dummy2");
        kenectContact2.setEmail("andres96666@gmail.com");

        kenectContact2.setCreated_at(createdAt);
        kenectContact2.setUpdated_at(createdAt);

        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .addHeader("Total-Pages", 2)
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(new KenectContactResponse(Collections.singletonList(kenectContact))))
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(new KenectContactResponse(Collections.singletonList(kenectContact2))))
                .addHeader("Content-Type", "application/json"));

        Flux<KenectContact> allContacts = kenectLabsClient.getAllContacts();

        StepVerifier.create(allContacts)
                .assertNext(kc -> {
                    Assertions.assertInstanceOf(KenectContact.class, kc);
                    Assertions.assertEquals(1, kc.getId());
                    Assertions.assertEquals("dummy", kc.getName());
                }
                )
                .assertNext(kc -> {
                    Assertions.assertInstanceOf(KenectContact.class, kc);
                    Assertions.assertEquals(2, kc.getId());
                    Assertions.assertEquals("dummy2", kc.getName());
                }
                ).verifyComplete();

        Mockito.verify(webClient, Mockito.times(1)).head();
        Mockito.verify(webClient, Mockito.times(3)).get();
    }

    @Test
    void testGetContactsNoResults() throws JsonProcessingException {

        mockWebServer.enqueue(new MockResponse().setResponseCode(500)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON));

        Flux<KenectContact> contactsByPage = kenectLabsClient.getContactsByPage(999);

        StepVerifier.create(contactsByPage)
                        .expectError(KenectClientException.class);

        Mockito.verify(webClient, Mockito.times(1)).get();
    }

    @AfterAll
    static void destroy() throws IOException {
        mockWebServer.shutdown();
    }

}
