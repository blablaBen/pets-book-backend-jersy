package com.app.server.services;


import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.models.PetProfile;
import com.app.server.models.User;
import com.app.server.util.APPCrypt;
import com.app.server.util.CheckAuthentication;
import com.app.server.util.MongoPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;


public class UserInterface {
    private static UserInterface self;
    private ObjectWriter ow;
    private MongoCollection<Document> collection = null;
    private MongoCollection<Document> petProfileCollection = null;
    FollowInterface followInterface = new FollowInterface();


    public UserInterface() {
        this.collection = MongoPool.getInstance().getCollection("user");
        this.petProfileCollection = MongoPool.getInstance().getCollection("pet_profile");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static UserInterface getInstance() {
        if (self == null)
            self = new UserInterface();
        return self;
    }


    public ArrayList<User> getAll(Integer userType, String profileName) {
        ArrayList<User> users = new ArrayList<User>();
        BasicDBObject query = new BasicDBObject();
        if (userType != null) {
            query.put("userType", userType);
        }
        if (profileName != null && !profileName.isEmpty()) {
            query.put("profileName", profileName);
        }
        FindIterable<Document> results = collection.find(query);
        if (results == null) {
            return users;
        }
        for (Document item : results) {
            User user = convertDocumentToUser(item);
            users.add(user);
        }
        return users;
    }


    public User getOne(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        Document item = collection.find(query).first();
        if (item == null) {
            return null;
        }
        User user = convertDocumentToUser(item);
        return user;

    }

    public User getByEmail(String email) {
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);

        Document item = collection.find(query).first();
        if (item == null) {
            return null;
        } else {
            return convertDocumentToUser(item);
        }
    }


    public User create(Object obj) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(obj));
            User user = convertJsonToUser(json);
            Document doc = convertUserToDocument(user);
            collection.insertOne(doc);
            ObjectId id = (ObjectId) doc.get("_id");
            user.setId(id.toString());
            followInterface.initFollow(doc.getObjectId("_id").toString());
            return user;
        } catch (JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public Object update(HttpHeaders headers, String id, Object request) {
        try {

            CheckAuthentication.check(headers, id);
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("key"))
                doc.append("key", json.getString("key"));
            if (json.has("email"))
                doc.append("email", json.getString("email"));
            if (json.has("password"))
                doc.append("password", APPCrypt.md5(json.getString("password")));
            if (json.has("profileName"))
                doc.append("profileName", json.getString("profileName"));
            if (json.has("userType"))
                doc.append("userType", json.getInt("userType"));
            if (json.has("userLevel"))
                doc.append("userLevel", json.getInt("userLevel"));
            if (json.has("userScore"))
                doc.append("userScore", json.getInt("userScore"));
            if (json.has("portraitUrl"))
                doc.append("portraitUrl", json.getString("portraitUrl"));


            Document set = new Document("$set", doc);
            collection.updateOne(query, set);
            return request;
        } catch (JsonProcessingException e) {
            System.out.println("Failed to update a document");
            return null;
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
            e.printStackTrace();
            return null;
        }

    }


    public Object updatePassword(String id, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("password"))
                doc.append("password", APPCrypt.md5(json.getString("password")));

            Document set = new Document("$set", doc);
            collection.updateOne(query, set);
            return request;
        } catch (JsonProcessingException e) {
            System.out.println("Failed to update a document");
            return null;
        } catch (Exception e) {
            System.out.println("Failed to update a document");
            e.printStackTrace();
            return null;
        }

    }


    public Object delete(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        collection.deleteOne(query);

        return new JSONObject();
    }

    public ArrayList<PetProfile> getAllPets(HttpHeaders headers, String ownerUserId) {
        try {

            CheckAuthentication.check(headers, ownerUserId);
            ArrayList<PetProfile> pets = new ArrayList<PetProfile>();

            BasicDBObject query = new BasicDBObject();
            query.put("ownerUserId", ownerUserId);

            FindIterable<Document> results = petProfileCollection.find(query);
            if (results == null) {
                return pets;
            }
            for (Document item : results) {
                PetProfile pet = convertDocumentToPetProfile(item);
                pets.add(pet);
            }
            return pets;
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
            e.printStackTrace();
            return null;
        }
    }

    public PetProfile createPetProfile(HttpHeaders headers, Object obj) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(obj));
            PetProfile pet = convertJsonToPetProfile(json);

            CheckAuthentication.check(headers, pet.getOwnerUserId());

            Document doc = convertPetProfileToDocument(pet);
            petProfileCollection.insertOne(doc);
            ObjectId id = (ObjectId) doc.get("_id");
            pet.setId(id.toString());
            return pet;
        } catch (JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
            e.printStackTrace();
            return null;
        }

    }

    public Object updatePetProfile(HttpHeaders headers, String id, String ownerUserId, Object request) {
        try {

            CheckAuthentication.check(headers, ownerUserId);

            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            query.put("ownerUserId", ownerUserId);

            Document doc = new Document();
            if (json.has("description"))
                doc.append("description", json.getString("description"));
            if (json.has("portraitUrl"))
                doc.append("portraitUrl", json.getString("portraitUrl"));


            Document set = new Document("$set", doc);
            petProfileCollection.updateOne(query, set);
            return request;
        } catch (JSONException e) {
            System.out.println("Failed to update a document");
            return null;

        } catch (JsonProcessingException e) {
            System.out.println("Failed to update a document");
            return null;
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
            e.printStackTrace();
            return null;
        }

    }

    public Object deletePetProfile(HttpHeaders headers, String id, String ownerUserId) {
        try {
            CheckAuthentication.check(headers, ownerUserId);

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            query.put("ownerUserId", ownerUserId);

            petProfileCollection.deleteOne(query);

            return new JSONObject();
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    private Document convertUserToDocument(User user) {

        Document doc = new Document("key", user.getKey())
                .append("email", user.getEmail())
                .append("password", user.getPassword())
                .append("profileName", user.getProfileName())
                .append("userType", user.getUserType())
                .append("userLevel", user.getUserLevel())
                .append("userScore", user.getUserScore())
                .append("portraitUrl", user.getPortraitUrl());
        return doc;
    }

    private Document convertPetProfileToDocument(PetProfile petProfile) {

        Document doc = new Document("ownerUserId", petProfile.getOwnerUserId())
                .append("description", petProfile.getDescription())
                .append("portraitUrl", petProfile.getPortraitUrl());
        return doc;
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

    private PetProfile convertDocumentToPetProfile(Document item) {
        PetProfile profile = new PetProfile(item.getString("ownerUserId"), item.getString("description"), item.getString("portraitUrl"));
        profile.setId(item.getObjectId("_id").toString());
        return profile;
    }

    private User convertJsonToUser(JSONObject json) throws Exception {

        User user = new User(
                json.getString("email"),
                json.getString("email"),
                APPCrypt.md5(json.getString("password")),
                json.getString("profileName"),
                json.getInt("userType"),
                json.getInt("userLevel"),
                json.getInt("userScore"),
                json.getString("portraitUrl")

        );
        return user;
    }

    private PetProfile convertJsonToPetProfile(JSONObject json) {
       PetProfile profile = new PetProfile(json.getString("ownerUserId"), json.getString("description"), json.getString("portraitUrl"));
       return profile;
    }

    /*void checkAuthentication(HttpHeaders headers, String id) throws Exception {
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70, "No Authorization Headers");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        if (id.compareTo(clearToken) != 0) {
            throw new APPUnauthorizedException(71, "Invalid token. Please try getting a new token");
        }
    }*/
}
