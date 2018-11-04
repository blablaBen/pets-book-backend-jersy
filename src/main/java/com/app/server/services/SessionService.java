package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.models.Session;
import com.app.server.models.User;
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
    private ObjectWriter ow;
    private MongoCollection<Document> userCollection = null;
    private MongoCollection<Document> carsCollection = null;

    private SessionService() {
        this.userCollection = MongoPool.getInstance().getCollection("user");
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
            //query.put("password", APPCrypt.encrypt(json.getString("password")));
            query.put("password", json.getString("password"));

            Document item = userCollection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No user found matching credentials");
            }

            User user = convertDocumentToUser(item);

            user.setId(item.getObjectId("_id").toString());
            return new Session(user);
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        } catch (APPBadRequestException e) {
            throw e;
        } catch (APPNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new APPInternalServerException(0, e.getMessage());
        }
    }


    private User convertDocumentToUser(Document item) {
        User user = new User(
                item.getString("key"),
                item.getString("email"),
                item.getString("password"),
                item.getString("profileName"),
                (int) item.get("userType"),
                (int) item.get("userLevel"),
                (int) item.get("userScore"),
                item.getString("portraitUrl")

        );
        user.setId(item.getObjectId("_id").toString());
        return user;
    }

} // end of main()
