package com.jobsity.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.jobsity.client.dto.KenectContact;
import com.jobsity.client.dto.KenectContactResponse;
import com.jobsity.exception.KenectClientException;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.spec.internal.HttpStatus;
import org.springframework.cloud.contract.spec.internal.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Slf4j
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class KenectLabsClientTest {

    private static final String BEARER_TOKEN = "skjdakj234j23adas-dummy";

    private static WebClient webClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private KenectLabsClient kenectLabsClient;

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort()).build();

    @BeforeAll
    public static void setup(){
        webClient = Mockito.spy(WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + BEARER_TOKEN)
                .baseUrl(wireMockExtension.baseUrl()).build());
    }

    @BeforeEach
    public void init(){
        kenectLabsClient = new KenectLabsClientImpl(webClient);
        ReflectionTestUtils.setField(kenectLabsClient, "CONTACTS_PATH", "/contacts");
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

        wireMockExtension.stubFor(get(urlPathEqualTo("/contacts"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + BEARER_TOKEN ))
                .withQueryParam("page", equalTo("1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaTypes.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(new KenectContactResponse(
                                Collections.singletonList(kenectContact))))));

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

        //Simulating/Mocking Kenect API
        wireMockExtension.stubFor(head(urlPathEqualTo("/contacts"))
                                .willReturn(aResponse()
                                        .withHeader("Total-Pages", "2")));

        wireMockExtension.stubFor(get(urlPathEqualTo("/contacts"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + BEARER_TOKEN ))
                .withQueryParam("page", equalTo("1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaTypes.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(new KenectContactResponse(
                                Collections.singletonList(kenectContact))))));

        wireMockExtension.stubFor(get(urlPathEqualTo("/contacts"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + BEARER_TOKEN))
                .withQueryParam("page", equalTo("2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaTypes.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(new KenectContactResponse(
                                Collections.singletonList(kenectContact2))))));

        Flux<KenectContact> allContacts = kenectLabsClient.getAllContacts();

        StepVerifier.create(allContacts.sort(Comparator.comparing(KenectContact::getId)))
                .assertNext(kc -> {
                            System.out.println(kc);
                    Assertions.assertInstanceOf(KenectContact.class, kc);
                    Assertions.assertEquals(1, kc.getId());
                    Assertions.assertEquals("dummy", kc.getName());
                }
                )
                .assertNext(kc -> {
                            System.out.println(kc);
                    Assertions.assertInstanceOf(KenectContact.class, kc);
                    Assertions.assertEquals(2, kc.getId());
                    Assertions.assertEquals("dummy2", kc.getName());
                }
                ).verifyComplete();

        Mockito.verify(webClient, Mockito.times(1)).head();
        Mockito.verify(webClient, Mockito.times(2)).get();

    }

    @Test
    void testGetContactsNoResults() throws JsonProcessingException {

        wireMockExtension.stubFor(get(urlPathEqualTo("/contacts"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + BEARER_TOKEN ))
                .withQueryParam("page", equalTo("999"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR)));

        Flux<KenectContact> contactsByPage = kenectLabsClient.getContactsByPage(999);

        StepVerifier.create(contactsByPage)
                        .expectError(KenectClientException.class);

        Mockito.verify(webClient, Mockito.times(1)).get();
    }

    @AfterEach
    public void clear(){
        Mockito.clearInvocations(webClient);
    }

}
