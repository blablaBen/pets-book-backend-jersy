package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.models.Notification;
import com.app.server.util.CheckAuthentication;
import com.app.server.util.MongoPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.ws.rs.core.HttpHeaders;
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

    public ArrayList<Notification> getAllNotification(HttpHeaders headers, String userId, String pageStr, String pageSizeStr) {
        try {
            CheckAuthentication.check(headers, userId);

            ArrayList<Notification> notifications = new ArrayList<Notification>();

            BasicDBObject query = new BasicDBObject();
            query.put("userId", userId);

            //Filter By Page
            if(pageSizeStr == null || pageStr == null) {
                throw new APPBadRequestException(55, "missing page and pageSize");
            }
            int skipPage = Integer.parseInt(pageStr);
            int pageSize = Integer.parseInt(pageSizeStr);
            FindIterable<Document> results = notificationCollection.find(query).sort(new BasicDBObject("date", 1)).skip(skipPage).limit(pageSize);
            if (results == null) {
                return notifications;
            }
            for (Document item : results) {
                Notification notification = convertDocumentToNotification(item);
                notifications.add(notification);
            }
            return notifications;

        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    public boolean createNotification(Notification noti) {
        try {
            Document item = this.convertNotificationDocument(noti);
            notificationCollection.insertOne(item);
            return true;
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    private Notification convertDocumentToNotification(Document item) {
        Notification notification = new Notification(
                item.getString("userId"),
                item.getString("type"),
                item.getString("content"),
                item.getBoolean("isRead"),
                item.getDate("date"),
                item.getString("postId")
        );
        notification.setId(item.getObjectId("_id").toString());
        return notification;
    }


    private Document convertNotificationDocument(Notification item) {
        Document doc = new Document("userId", item.getUserId())
                .append("type", item.getType())
                .append("content", item.getContent())
                .append("date", item.getDate())
                .append("isRead", item.isRead())
                .append("postId", item.getPostId());
        return doc;
    }
}

