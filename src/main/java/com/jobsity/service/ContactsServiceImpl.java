package com.jobsity.service;

import com.jobsity.client.KenectLabsClient;
import com.jobsity.client.dto.KenectContact;
import com.jobsity.controller.dto.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ContactsServiceImpl implements ContactsService {

    @Value("${kenect.api.source}")
    private String KENECT_LABS_SOURCE;

    private final KenectLabsClient kenectLabsClient;

    @Autowired
    public ContactsServiceImpl(KenectLabsClient KenectLabsClient){
        this.kenectLabsClient = KenectLabsClient;
    }

    @Override
    public Flux<Contact> getContacts(Integer page) {
        Flux<KenectContact> contacts;
        if(page != null){
            contacts = kenectLabsClient.getContactsByPage(page);
        } else {
            contacts = kenectLabsClient.getAllContacts();
        }

        return contacts
                .map(kenectContact -> Contact.builder()
                        .source(KENECT_LABS_SOURCE)
                        .id(kenectContact.getId())
                        .name(kenectContact.getName())
                        .email(kenectContact.getEmail())
                        .created_at(kenectContact.getCreated_at())
                        .updated_at(kenectContact.getUpdated_at())
                        .build());
    }

}
