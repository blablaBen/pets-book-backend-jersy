package com.app.server.services;


import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.util.CheckAuthentication;
import com.app.server.util.MongoPool;
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

public class FollowInterface {

    private static FollowInterface self;
    private ObjectWriter ow;
    private MongoCollection<Document> collection = null;

    private FollowInterface() {
        this.collection = MongoPool.getInstance().getCollection("follow");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public static FollowInterface getInstance() {
        if (self == null)
            self = new FollowInterface();
        return self;
    }

    public ArrayList<String> getAll(HttpHeaders headers, String userId, int type) {
        try {
            CheckAuthentication.check(headers, userId);
            BasicDBObject query = new BasicDBObject();
            query.put("userId", userId);

            ArrayList<String> follows = new ArrayList<String>();

            FindIterable<Document> results = collection.find(query);
            if (results == null) {
                return follows;
            }
            for (Document item : results) {
                if (type == 1) {
                    follows.addAll((ArrayList<String>) item.get("following"));
                } else {
                    follows.addAll((ArrayList<String>) item.get("followed"));
                }
            }
            return follows;

        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }

    }


    public boolean addFollow(HttpHeaders headers, String userId, Object request) {
        try {
            CheckAuthentication.check(headers, userId);

            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            String followId = json.getString("followId");
            BasicDBObject query = new BasicDBObject();
            query.put("userId", userId);

            Document item = collection.find(query).first();
            if (item == null) {
                return false;
            }
            ArrayList<String> following = (ArrayList<String>) item.get("following");
            following.add(followId);


            BasicDBObject update = new BasicDBObject();
            update.put("_id", item.getObjectId("_id"));

            Document doc = new Document("userId", userId)
                    .append("following", following)
                    .append("followed", item.get("followed"));

            Document set = new Document("$set", doc);
            collection.updateOne(update, set);

            addFollower(followId, userId);

            return true;
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to create a document");
            return false;
        }

    }

    public boolean addFollower(String userId, String followId) {
        try {

            BasicDBObject query = new BasicDBObject();
            query.put("userId", userId);

            Document item = collection.find(query).first();
            if (item == null) {
                return false;
            }
            ArrayList<String> follower = (ArrayList<String>) item.get("followed");
            follower.add(followId);


            BasicDBObject update = new BasicDBObject();
            update.put("_id", item.getObjectId("_id"));

            Document doc = new Document("userId", userId)
                    .append("following", item.get("following"))
                    .append("followed", follower);

            Document set = new Document("$set", doc);
            collection.updateOne(update, set);

            return true;
        } catch (Exception e) {
            System.out.println("Failed to create a document");
            return false;
        }

    }


    public Object update(HttpHeaders headers, String id, JSONObject obj) {
        try {

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (obj.has("following"))
                doc.append("following", obj.getJSONArray("following"));
            if (obj.has("followed"))
                doc.append("followed", obj.getJSONArray("followed"));

            Document set = new Document("$set", doc);
            collection.updateOne(query, set);

        } catch (JSONException e) {
            System.out.println("Failed to addFollow a document");

        }
        return obj;
    }

    public void initFollow(String userId) {
        try {
            Document doc = new Document("userId", userId)
                    .append("following", new ArrayList<>())
                    .append("followed", new ArrayList<>());

            collection.insertOne(doc);
        } catch (Exception e) {

        }
    }


    public void deleteFollowing(HttpHeaders headers, String userId, String followId) {
        try {
            CheckAuthentication.check(headers, userId);
            BasicDBObject query = new BasicDBObject();
            query.put("userId", userId);

            Document item = collection.find(query).first();
            if (item == null) {
                return;
            }
            ArrayList<String> following = (ArrayList<String>) item.get("following");
            following.remove(followId);


            BasicDBObject update = new BasicDBObject();
            update.put("_id", item.getObjectId("_id"));

            Document doc = new Document("userId", userId)
                    .append("following", following)
                    .append("followed", item.get("followed"));

            Document set = new Document("$set", doc);
            collection.updateOne(update, set);

            deleteFollower(followId, userId);
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to delete a following");

        }
    }

    public void deleteFollower(String userId, String followId) {
        try {

            BasicDBObject query = new BasicDBObject();
            query.put("userId", userId);

            Document item = collection.find(query).first();
            if (item == null) {
                return;
            }
            ArrayList<String> follower = (ArrayList<String>) item.get("followed");
            follower.remove(followId);


            BasicDBObject update = new BasicDBObject();
            update.put("_id", item.getObjectId("_id"));

            Document doc = new Document("userId", userId)
                    .append("following", item.get("following"))
                    .append("followed", follower);

            Document set = new Document("$set", doc);
            collection.updateOne(update, set);

        } catch (Exception e) {
            System.out.println("Failed to delete a follower");

        }
    }
}
