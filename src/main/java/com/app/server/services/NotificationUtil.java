package com.app.server.services;

import com.app.server.enumeration.NotificationType;
import com.app.server.models.Notification;
import com.app.server.models.PostComment;
import com.app.server.models.PostStatus;

import java.util.ArrayList;
import java.util.Date;

public class NotificationUtil {
    private static NotificationUtil self;
   private NotificationService notificationService;
   private UserService userService;

   public NotificationUtil() {
       this.notificationService = NotificationService.getInstance();
       this.userService = UserService.getInstance();
   }

    public static NotificationUtil getInstance(){
        if (self == null)
            self = new NotificationUtil();
        return self;
    }

   public boolean addNotificationWhenCommentIsAdded(PostComment comment, ArrayList<PostComment> commentsUnderPost, PostStatus posterStatus) {
       Date date = new Date();
       for(PostComment item : commentsUnderPost) {
           try {
               String username = (userService.getOne(comment.getUserId())).getProfileName();
               String contentForComment = username + " has given a comment on the post you have commented";
               Notification notiItemForComment = new Notification(item.getUserId(), NotificationType.NEW_COMMENT.getValue(), contentForComment, false, date, comment.getPostId());
               notificationService.createNotification(notiItemForComment);
           } catch (Exception e) {
               e.printStackTrace();
           }
       }

       try {
           String posterUsername = (userService.getOne(comment.getUserId())).getProfileName();
           String contentForPost = posterUsername + " has given a comment on your post";
           Notification notiItemForPost = new Notification(posterStatus.getUserId(), NotificationType.NEW_COMMENT.getValue(), contentForPost, false,  date, comment.getPostId());
           notificationService.createNotification(notiItemForPost);
       } catch (Exception e) {
           e.printStackTrace();
       }

       return true;
   }

   public boolean addNotificationWhenFollowerIsAdded(String followerUserId, String followedUserId) {
       try {
           String usernameOfFollower = (userService.getOne(followerUserId)).getProfileName();
           String content = usernameOfFollower + " is following you";
           Notification notiItem = new Notification(followedUserId, NotificationType.NEW_FOLLOWER.getValue(), content,false, new Date(), null);
           notificationService.createNotification(notiItem);
           return true;
       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }


   }

   public boolean addNotificationWhenChatRoomIsAdded(String vetId, String petLoverId, String chatroomId) {
       try {
           String usernameOfPetLover = (userService.getOne(petLoverId)).getProfileName();
           String content = usernameOfPetLover + " wants to talk with you";
           Notification notiItem = new Notification(vetId, NotificationType.NEW_CHAT.getValue(), content,false, new Date(), chatroomId);
           notificationService.createNotification(notiItem);
           return true;
       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }
   }

    public boolean addNotificationWhenMessageIsAdded(String senderId, String receiverId,  String chatroomId) {
        try {
            String username = (userService.getOne(senderId)).getProfileName();
            String content = username + " send you a message";
            Notification notiItem = new Notification(receiverId, NotificationType.NEW_MESSAGE.getValue(), content,false, new Date(), chatroomId);
            notificationService.createNotification(notiItem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
