package com.ricana.activity_midterm.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContactService {

    private final OAuth2AuthorizedClientService clientService;

    public ContactService(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    private String fetchAccessToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
            if (client != null) {
                return client.getAccessToken().getTokenValue();
            }
        }
        throw new RuntimeException("Failed to fetch OAuth2 access token.");
    }

    private PeopleService buildPeopleService() {
        return new PeopleService.Builder(new NetHttpTransport(), new GsonFactory(), request -> {
            request.getHeaders().setAuthorization("Bearer " + fetchAccessToken());
        }).setApplicationName("Google Contacts App").build();
    }

    public List<Person> fetchContacts() throws IOException {
        PeopleService service = buildPeopleService();
        ListConnectionsResponse response = service.people().connections()
                .list("people/me")
                .setPersonFields("names,emailAddresses,phoneNumbers")
                .execute();
        return response.getConnections() != null ? response.getConnections() : new ArrayList<>();
    }

    public Person addContact(String firstName, String lastName, String[] emails, String[] phoneNumbers) throws IOException {
        PeopleService service = buildPeopleService();
        Person contact = new Person();

        Name name = new Name().setGivenName(firstName).setFamilyName(lastName);
        contact.setNames(List.of(name));

        if (emails != null && emails.length > 0) {
            List<EmailAddress> emailList = new ArrayList<>();
            for (String email : emails) {
                if (email != null && !email.trim().isEmpty()) {
                    emailList.add(new EmailAddress().setValue(email));
                }
            }
            contact.setEmailAddresses(emailList);
        }

        if (phoneNumbers != null && phoneNumbers.length > 0) {
            List<PhoneNumber> phoneList = new ArrayList<>();
            for (String phone : phoneNumbers) {
                if (phone != null && !phone.trim().isEmpty()) {
                    phoneList.add(new PhoneNumber().setValue(phone));
                }
            }
            contact.setPhoneNumbers(phoneList);
        }

        return service.people().createContact(contact).execute();
    }

    public void modifyContact(String contactId, String firstName, String lastName, String[] emails, String[] phoneNumbers) throws IOException {
        PeopleService service = buildPeopleService();
        Person contact = service.people().get(contactId)
                .setPersonFields("names,emailAddresses,phoneNumbers")
                .execute();

        Person updatedContact = new Person();
        updatedContact.setEtag(contact.getEtag());
        updatedContact.setNames(List.of(new Name().setGivenName(firstName).setFamilyName(lastName)));

        if (emails != null && emails.length > 0) {
            List<EmailAddress> emailList = new ArrayList<>();
            for (String email : emails) {
                if (email != null && !email.trim().isEmpty()) {
                    emailList.add(new EmailAddress().setValue(email));
                }
            }
            updatedContact.setEmailAddresses(emailList);
        }

        if (phoneNumbers != null && phoneNumbers.length > 0) {
            List<PhoneNumber> phoneList = new ArrayList<>();
            for (String phone : phoneNumbers) {
                if (phone != null && !phone.trim().isEmpty()) {
                    phoneList.add(new PhoneNumber().setValue(phone));
                }
            }
            updatedContact.setPhoneNumbers(phoneList);
        }

        service.people().updateContact(contactId, updatedContact)
                .setUpdatePersonFields("names,emailAddresses,phoneNumbers")
                .execute();
    }

    public void removeContact(String contactId) throws IOException {
        PeopleService service = buildPeopleService();
        service.people().deleteContact(contactId).execute();
    }
}