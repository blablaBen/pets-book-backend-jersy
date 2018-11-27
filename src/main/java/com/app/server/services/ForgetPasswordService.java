package com.app.server.services;

import com.app.server.models.ForgetPassword;
import com.app.server.models.User;
import com.app.server.util.MongoPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ForgetPasswordService {

    private static ForgetPasswordService self;
    private ObjectWriter ow;
    private MongoCollection<Document> collection = null;

    private UserInterface userInterface;

    private ForgetPasswordService() {
        this.collection = MongoPool.getInstance().getCollection("forgetPassword");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        userInterface = UserInterface.getInstance();

    }

    public static ForgetPasswordService getInstance() {
        if (self == null)
            self = new ForgetPasswordService();
        return self;
    }

    public boolean create(Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Document doc = new Document("email", json.get("email"))
                    .append("token", getSaltString())
                    .append("expiredTime", afterDate());

            collection.insertOne(doc);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to create a forget password");
            return false;
        }
    }

    public ForgetPassword getByToken(String token) {
        try {

            BasicDBObject query = new BasicDBObject();
            query.put("token", token);

            Document item = collection.find(query).first();
            if (item == null) {
                return null;
            } else {
                return convertDocumentToForget(item);
            }

        } catch (Exception e) {
            System.out.println("Failed to create a forget password");
            return null;
        }
    }

    private ForgetPassword convertDocumentToForget(Document item) {
        ForgetPassword user = new ForgetPassword(
                item.getString("email"),
                item.getString("token"),
                item.getDate("expireTime")
        );
        user.setId(item.getObjectId("_id").toString());
        return user;
    }

    private Date afterDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 6);
        Date myDate = cal.getTime();
        return myDate;
    }

    private String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public boolean resetPassword(Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            if (json.get("email") == null || json.get("password") == null) {
                return false;
            }
            User user = userInterface.getByEmail(json.getString("email"));
            if (user == null) return false;

            userInterface.updatePassword(user.getId(), request);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to create a forget password");
            return false;
        }
    }
}
