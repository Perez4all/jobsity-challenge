package com.jobsity.client;

import com.jobsity.client.dto.KenectContact;
import com.jobsity.client.dto.KenectContactResponse;
import com.jobsity.exception.KenectClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class KenectLabsClientImpl implements KenectLabsClient{

    @Value("${kenect.api.contacts.path}")
    private String CONTACTS_PATH;

    private final WebClient webClient;

    @Autowired
    public KenectLabsClientImpl(WebClient webClient){
        this.webClient = webClient;
    }

    /**
     * Get contacts by page
     * @param page Page number
     * @return A reactive flux with the contacts in page
     */
    @Override
    public Flux<KenectContact> getContactsByPage(Integer page){
        return webClient.get().uri(builder -> buildUrl(builder, page))
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    throw new KenectClientException("No results found");
                })
                .bodyToMono(KenectContactResponse.class)
                .map(KenectContactResponse::getContacts)
                .flatMapMany(Flux::fromIterable);
    }

    /**
     * Get all the contacts in all pages
     * @return Reactive Flux of all {@link KenectContact}
     */
    @Override
    public Flux<KenectContact> getAllContacts(){
       return webClient.head().uri(builder -> buildUrl(builder, null)).retrieve()
               .toBodilessEntity()
               .map(ResponseEntity::getHeaders)
               .flatMapMany(this::getFluxFromPageRange)
               .flatMap(this::getContactsByPage)
               .sort(Comparator.comparing(KenectContact::getId));
    }

    /**
     * To create multiple requests per page
     * @param headers Response entity headers
     * @return a Flux of pages
     */
    private Flux<Integer> getFluxFromPageRange(HttpHeaders headers){
        List<String> totalPages = headers.get("Total-Pages");
        if(totalPages != null) {
            int pages = Integer.parseInt(totalPages.get(0));
            return Flux.range(1, pages);
        }
        return Flux.empty();
    }

    /**
     * Creates the URI to request
     * @param builder URI Builder
     * @param page Page number
     * @return URI to request
     */
    private URI buildUrl(UriBuilder builder, Integer page){
            builder = builder.path(CONTACTS_PATH);
            if(page != null) {
                builder.queryParam("page", page).build();
            }
            return builder.build();
    }
}
