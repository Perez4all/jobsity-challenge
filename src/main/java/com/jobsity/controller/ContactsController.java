package com.jobsity.controller;

import com.jobsity.controller.dto.Contact;
import com.jobsity.service.ContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/contacts")
public class ContactsController {

    private final ContactsService contactsService;

    @Autowired
    public ContactsController(ContactsService contactsService){
        this.contactsService = contactsService;
    }

    @GetMapping
    public ResponseEntity<Flux<Contact>> getAllContacts(@RequestParam(required = false) Integer page){
        return ResponseEntity.ok(contactsService.getContacts(page));
    }

}
