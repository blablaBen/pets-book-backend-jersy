package com.app.server.services;

import com.app.server.models.Notification;
import com.app.server.models.PetProfile;
import com.app.server.models.User;
import com.app.server.util.MongoPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;

public class NotificationService {
    private static NotificationService self;
    private ObjectWriter ow;
    private MongoCollection<Document> notificationCollection = null;

    private NotificationService() {
        this.notificationCollection = MongoPool.getInstance().getCollection("notification");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static NotificationService getInstance(){
        if (self == null)
            self = new NotificationService();
        return self;
    }

    public ArrayList<Notification> getAllNotification(String userId) {
        ArrayList<Notification> notifications = new ArrayList<Notification>();

        BasicDBObject query = new BasicDBObject();
        query.put("userId", userId);

        FindIterable<Document> results = notificationCollection.find(query);
        if (results == null) {
            return notifications;
        }
        for (Document item : results) {
            Notification notification = convertDocumentToNotification(item);
            notifications.add(notification);
        }
        return notifications;
    }

    private Notification convertDocumentToNotification(Document item) {
        Notification notification = new Notification(
                item.getString("userId"),
                item.getString("type"),
                item.getString("content"),
                item.getBoolean("isRead")
        );
        notification.setId(item.getObjectId("_id").toString());
        return notification;
    }
}

