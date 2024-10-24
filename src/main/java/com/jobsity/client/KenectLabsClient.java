package com.jobsity.client;

import com.jobsity.client.dto.KenectContact;
import reactor.core.publisher.Flux;

public interface KenectLabsClient {
    Flux<KenectContact> getContactsByPage(Integer page);
    Flux<KenectContact> getAllContacts();
}
