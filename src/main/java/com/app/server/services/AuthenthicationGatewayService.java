package com.app.server.services;

import com.app.server.models.User;
import com.app.server.models.UserSSOInformation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class AuthenthicationGatewayService {
    private static AuthenthicationGatewayService self;
    private ObjectWriter ow;

    private AuthenthicationGatewayService() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public static AuthenthicationGatewayService getInstance() {
        if (self == null)
            self = new AuthenthicationGatewayService();
        return self;
    }

    public UserSSOInformation ssoLogin(String tokenId) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList("541115279526-k7ps60te3gfd3os9satu7sjb0ipsfrbj.apps.googleusercontent.com"))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();
        // (Receive idTokenString by HTTPS POST)


        GoogleIdToken idToken = verifier.verify(tokenId);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            // Get profile information from payload
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            UserSSOInformation user =  new UserSSOInformation();
            user.setEmail(email);
            user.setPortraitUrl(pictureUrl);
            user.setProfileName(name);
            return user;
        }

        return null;
    }
}
