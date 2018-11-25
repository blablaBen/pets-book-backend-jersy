package com.app.server.services;

import com.app.server.enumeration.UserType;
import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.models.ChatMessage;
import com.app.server.models.ChatRoom;
import com.app.server.models.User;
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
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;
import java.util.Date;

public class MessagingService {
    private static MessagingService self;
    private NotificationUtil notificationUtil;
    private ObjectWriter ow;
    private MongoCollection<Document> chatRoomCollection = null;
    private MongoCollection<Document> chatMessageCollection = null;

    private MessagingService() {
        this.chatRoomCollection = MongoPool.getInstance().getCollection("chat_room");
        this.chatMessageCollection = MongoPool.getInstance().getCollection("chat_message");
        this.notificationUtil = NotificationUtil.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public static MessagingService getInstance() {
        if (self == null)
            self = new MessagingService();
        return self;
    }

    public ArrayList<ChatRoom> getAllChatRooms(HttpHeaders headers, String userId, Integer userType) {
        try {
            CheckAuthentication.check(headers, userId);
            ArrayList<ChatRoom> rooms = new ArrayList<ChatRoom>();
            BasicDBObject query = new BasicDBObject();

            if (userType == UserType.PET_OWNER.getValue()) {
                query.put("petOwnerUserId", userId);
            } else {
                query.put("vetUserId", userId);
            }

            FindIterable<Document> results = chatRoomCollection.find(query);
            if (results == null) {
                return rooms;
            }

            for (Document item : results) {
                ChatRoom room = convertDocumentToChatRoom(item);
                rooms.add(room);
            }

            return rooms;
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }


    public ChatRoom createRoom(HttpHeaders headers, Object obj) {

        try {
            CheckAuthentication.onlyCheckAuthenthicationProvided(headers);

            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(obj));
            ChatRoom room = convertJsonToChatRoom(json);
            Document doc = convertChatRoomToDocument(room);
            chatRoomCollection.insertOne(doc);
            ObjectId id = (ObjectId) doc.get("_id");
            room.setId(id.toString());

            notificationUtil.addNotificationWhenChatRoomIsAdded(room.getVetUserId(), room.getPetOwnerUserId(), room.getId());
            return room;
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(55, "Json is invalid");
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }

    }

    public ArrayList<ChatMessage> getAllChatMessage(HttpHeaders headers, String chatRoomId, String userType, String pageSizeStr, String pageStr) {
        try {
            if(userType == null) {
                throw new APPBadRequestException(55, "UserType is required");
            }

            validateAuthenthication(headers, chatRoomId, Integer.parseInt(userType));

            ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();

            BasicDBObject query = new BasicDBObject();
            query.put("chatRoomId", chatRoomId);

            //Filter By Page
            if (pageSizeStr == null || pageStr == null) {
                throw new APPBadRequestException(55, "Missing page and pageSize");
            }
            int skipPage = Integer.parseInt(pageStr);
            int pageSize = Integer.parseInt(pageSizeStr);
            FindIterable<Document> results = chatMessageCollection.find(query).sort(new BasicDBObject("time", 1)).skip(skipPage).limit(pageSize);
            if (results == null) {
                return messages;
            }

            for (Document item : results) {
                ChatMessage message = convertDocumentToChatMessage(item);
                messages.add(message);
            }

            return messages;
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (APPBadRequestException ab) {
            ab.printStackTrace();
            throw ab;
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    private boolean validateAuthenthication(HttpHeaders headers, String chatRoomId, Integer userType) throws Exception{
        ChatRoom chatRoom = getChatRoomItem(chatRoomId);
        String userId = (userType == UserType.PET_OWNER.getValue()) ? chatRoom.getPetOwnerUserId() : chatRoom.getVetUserId();
        CheckAuthentication.check(headers, userId);
        return  true;
    }

    private ChatRoom getChatRoomItem(String chatRoomId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(chatRoomId));

        Document item = chatRoomCollection.find(query).first();
        return convertDocumentToChatRoom(item);
    }


    public ChatMessage createMessage(HttpHeaders headers, Object obj) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(obj));
            ChatMessage message = convertJsonToChatMessage(json);

            CheckAuthentication.check(headers, message.getUserId());

            Document doc = convertChatMessageToDocument(message);
            chatMessageCollection.insertOne(doc);
            ObjectId id = (ObjectId) doc.get("_id");
            message.setId(id.toString());

            addNotificationWhenCreateMessage(message);
            return message;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new APPBadRequestException(55, "Json is invalid:" + e.getMessage());
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }

    }

    private void addNotificationWhenCreateMessage(ChatMessage message) {
        try {
            String senderId = message.getUserId();
            ChatRoom room = getChatRoomItem(message.getChatRoomId());
            String receiverId = room.getVetUserId().equals(senderId) ? room.getPetOwnerUserId() : room.getVetUserId();
            notificationUtil.addNotificationWhenMessageIsAdded(senderId, receiverId, message.getChatRoomId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private ChatRoom convertJsonToChatRoom(JSONObject json) {
        ChatRoom room = new ChatRoom(
                json.getString("petOwnerUserId"),
                json.getString("vetUserId")
        );
        return room;
    }

    private ChatMessage convertJsonToChatMessage(JSONObject json) {
        ChatMessage message = new ChatMessage(
                json.getString("chatRoomId"),
                json.getString("userId"),
                json.getString("message"),
                new Date()
        );
        return message;
    }

    private Document convertChatRoomToDocument(ChatRoom room) {
        Document doc = new Document("petOwnerUserId", room.getPetOwnerUserId())
                .append("vetUserId", room.getVetUserId());
        return doc;
    }

    private Document convertChatMessageToDocument(ChatMessage message) {
        Document doc = new Document("chatRoomId", message.getChatRoomId())
                .append("userId", message.getUserId())
                .append("message", message.getMessage())
                .append("time", message.getTime());
        return doc;
    }

    private ChatRoom convertDocumentToChatRoom(Document item) {
        ChatRoom room =  new ChatRoom(item.getString("petOwnerUserId"), item.getString("vetUserId"));
        room.setId(item.getObjectId("_id").toString());
        return room;
    }

    private ChatMessage convertDocumentToChatMessage(Document item) {
        ChatMessage message =  new ChatMessage(item.getString("chatRoomId"), item.getString("userId"), item.getString("message"), item.getDate("time"));
        message.setId(item.getObjectId("_id").toString());
        return message;
    }

}
