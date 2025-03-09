package com.ricana.activity_midterm.controller;

import com.google.api.services.people.v1.model.Person;
import com.ricana.activity_midterm.service.ContactService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class WebController {

    private final ContactService contactService;

    public WebController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contacts")
    public String displayContactsPage(Model model) {
        try {
            List<Person> contacts = contactService.fetchContacts();
            model.addAttribute("contacts", contacts);
            return "contacts"; // Render the contacts page
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Unable to load contacts.");
            return "error"; // Render the error page
        }
    }

    @PostMapping("/api/contacts/create")
    public String addContact(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String[] emails,
            @RequestParam(required = false) String[] phoneNumbers) throws IOException {
        Person newContact = contactService.addContact(firstName, lastName, emails, phoneNumbers);
        System.out.println("New contact added: " + newContact.getResourceName());
        return "redirect:/contacts";
    }

    @PostMapping("/api/contacts/update")
    public String modifyContact(
            @RequestParam String contactId,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String[] emails,
            @RequestParam(required = false) String[] phoneNumbers) throws IOException {
        contactService.modifyContact(contactId, firstName, lastName, emails, phoneNumbers);
        System.out.println("Contact updated: " + contactId);
        return "redirect:/contacts";
    }

    @PostMapping("/api/contacts/delete")
    public String removeContact(@RequestParam String contactId) throws IOException {
        contactService.removeContact(contactId);
        System.out.println("Contact deleted: " + contactId);
        return "redirect:/contacts";
    }
}