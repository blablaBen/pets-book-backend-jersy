package com.app.server.services;


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

import java.util.ArrayList;

public class FollowInterface {


    private ObjectWriter ow;
    private MongoCollection<Document> collection = null;

    public FollowInterface() {
        this.collection = MongoPool.getInstance().getCollection("follow");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }


    public ArrayList<String> getAll(String userId, int type) {

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
    }


    public boolean addFollow(String userId, Object request) {
        try {

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


    public Object update(String id, JSONObject obj) {
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


    public void deleteFollowing(String userId, String followId) {
        try {

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
