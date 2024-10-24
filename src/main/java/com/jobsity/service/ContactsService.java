package com.jobsity.service;

import com.jobsity.controller.dto.Contact;
import reactor.core.publisher.Flux;

public interface ContactsService {
    Flux<Contact> getContacts(Integer page);
}
