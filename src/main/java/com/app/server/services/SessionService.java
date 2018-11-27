package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.models.Session;
import com.app.server.models.User;
import com.app.server.models.UserSSOInformation;
import com.app.server.util.APPCrypt;
import com.app.server.util.MongoPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONObject;

/**
 * Services run as singletons
 */

public class SessionService {
    private static SessionService self;
    private static UserService userService;
    private AuthenthicationGatewayService authenthicationGatewayService;
    private ObjectWriter ow;
    private MongoCollection<Document> userCollection = null;
    private MongoCollection<Document> carsCollection = null;

    private SessionService() {
        this.userCollection = MongoPool.getInstance().getCollection("user");
        this.userService = UserService.getInstance();
        this.authenthicationGatewayService = AuthenthicationGatewayService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static SessionService getInstance() {
        if (self == null)
            self = new SessionService();
        return self;
    }

    public Session create(Object request) {

        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
            if (!json.has("email"))
                throw new APPBadRequestException(55, "missing emailAddress");
            if (!json.has("password"))
                throw new APPBadRequestException(55, "missing password");
            BasicDBObject query = new BasicDBObject();

            query.put("email", json.getString("email"));
            query.put("password", APPCrypt.md5(json.getString("password")));
            //query.put("password", json.getString("password"));

            Document item = userCollection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No user found matching credentials");
            }

            User user = convertDocumentToUser(item);

            user.setId(item.getObjectId("_id").toString());
            return new Session(user);
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        } catch (Exception e) {
            throw new APPInternalServerException(0, e.getMessage());
        }
    }


    public Session createFromToken(Object request) {
        try {
            JSONObject json = new JSONObject(ow.writeValueAsString(request));
            UserSSOInformation userInformationFromSSO = authenthicationGatewayService.ssoLogin(json.getString("idtoken"));

            if (userInformationFromSSO != null) {
                BasicDBObject query = new BasicDBObject();
                query.put("email", userInformationFromSSO.getEmail());
                query.put("isSSOUser", true);
                Document item = userCollection.find(query).first();
                User user = new User();
                if (item == null) {
                   // add new User;
                    User newUSerObj = new User(
                            userInformationFromSSO.getEmail(),
                            userInformationFromSSO.getEmail(),
                            "",
                            userInformationFromSSO.getProfileName(),
                            1,
                            userInformationFromSSO.getPortraitUrl());
                    newUSerObj.setSSOUser(true);
                    user = userService.createFromSSO(newUSerObj);
                } else {
                    user = convertDocumentToUser(item);
                    user.setId(item.getObjectId("_id").toString());
                }

                return new Session(user);
            } else {
                throw new APPBadRequestException(33, "Invalid ID token.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(0, e.getMessage());
        }
    }


    private User convertDocumentToUser(Document item) throws Exception {
        User user = new User(
                item.getString("key"),
                item.getString("email"),
                item.getString("password"),
                item.getString("profileName"),
                (int) item.get("userType"),
                (int) item.get("userLevel"),
                Integer.parseInt(APPCrypt.decrypt(item.getString("userScore"))),
                item.getString("portraitUrl")
        );
        user.setSSOUser(item.getBoolean("isSSOUser"));
        return user;
    }
} // end of main()
