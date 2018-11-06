package com.app.server.services;

import com.app.server.enumeration.UserType;
import com.app.server.models.ChatMessage;
import com.app.server.models.ChatRoom;
import com.app.server.models.User;
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

import java.util.ArrayList;
import java.util.Date;

public class MessagingService {
    private static MessagingService self;
    private ObjectWriter ow;
    private MongoCollection<Document> chatRoomCollection = null;
    private MongoCollection<Document> chatMessageCollection = null;

    private MessagingService() {
        this.chatRoomCollection = MongoPool.getInstance().getCollection("chat_room");
        this.chatMessageCollection = MongoPool.getInstance().getCollection("chat_message");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public static MessagingService getInstance() {
        if (self == null)
            self = new MessagingService();
        return self;
    }

    public ArrayList<ChatRoom> getAllChatRooms(String userId, Integer userType) {
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
    }


    public ChatRoom createRoom(Object obj) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(obj));
            ChatRoom room = convertJsonToChatRoom(json);
            Document doc = convertChatRoomToDocument(room);
            chatRoomCollection.insertOne(doc);
            ObjectId id = (ObjectId) doc.get("_id");
            room.setId(id.toString());

            return room;
        } catch (JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        }

    }

    public ArrayList<ChatMessage> getAllChatMessage(String chatRoomId) {
        ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();

        BasicDBObject query = new BasicDBObject();
        query.put("chatRoomId", chatRoomId);

        FindIterable<Document> results = chatMessageCollection.find(query);
        if (results == null) {
            return messages;
        }

        for (Document item : results) {
            ChatMessage message = convertDocumentToChatMessage(item);
            messages.add(message);
        }

        return messages;
    }


    public ChatMessage createMessage(String chatRoomId, Object obj) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(obj));
            ChatMessage message = convertJsonToChatMessage(json);
            Document doc = convertChatMessageToDocument(message);
            chatMessageCollection.insertOne(doc);
            ObjectId id = (ObjectId) doc.get("_id");
            message.setId(id.toString());

            return message;
        } catch (JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
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
