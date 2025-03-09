package com.ricana.activity_midterm.controller;

import com.google.api.services.people.v1.model.Person;
import com.ricana.activity_midterm.service.ContactService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactsApiController {

    private final ContactService contactService;

    public ContactsApiController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public List<Person> fetchContacts() throws IOException {
        List<Person> contacts = contactService.fetchContacts();
        System.out.println("Contacts retrieved: " + contacts.size());
        return contacts;
    }
}