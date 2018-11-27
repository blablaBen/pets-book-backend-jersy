package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.models.PostComment;
import com.app.server.models.PostStatus;
import com.app.server.util.CheckAuthentication;
import com.app.server.util.MongoPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedService {
    private static FeedService self;
    private NotificationUtil notificationUtil;
    private ObjectWriter ow;
    private MongoCollection<Document> postedStatusCollection = null;
    private MongoCollection<Document> commentCollection = null;

    private UserLevelService userLevelService;

    private FeedService() {
        this.postedStatusCollection = MongoPool.getInstance().getCollection("poststatus");
        this.commentCollection = MongoPool.getInstance().getCollection("comment");
        this.userLevelService = UserLevelService.getInstance();
        this.notificationUtil = NotificationUtil.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static FeedService getInstance(){
        if (self == null)
            self = new FeedService();
        return self;
    }

    public PostStatus getOne(HttpHeaders headers, String id)   {
        try {
            CheckAuthentication.onlyCheckAuthenthicationProvided(headers);
            return queryPostStatus(id);
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    public PostStatus queryPostStatus(String id) {
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            Document item = postedStatusCollection.find(query).first();

            if (item == null) {
                return null;
            }

            PostStatus status = convertDocumentToPostedStatus(item);
            return status;
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (APPBadRequestException b) {
            b.printStackTrace();
            throw b;
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    public ArrayList<PostStatus> getAll(HttpHeaders headers, String pageSizeStr, String pageStr, String showOnlyUserId) {
        try {
            CheckAuthentication.onlyCheckAuthenthicationProvided(headers);
            ArrayList<PostStatus> postList = new ArrayList<PostStatus>();

            //Filter By UserId
            BasicDBObject query = new BasicDBObject();
            if(showOnlyUserId != null) {
                query.put("userId", showOnlyUserId);
            }

            //Filter By Page
            if(pageSizeStr == null || pageStr == null) {
                throw new APPBadRequestException(55, "missing page and pageSize");
            }
            int skipPage = Integer.parseInt(pageStr);
            int pageSize = Integer.parseInt(pageSizeStr);
            FindIterable<Document> results = postedStatusCollection.find(query).sort(new BasicDBObject("date", 1)).skip(skipPage).limit(pageSize);


            if (results == null) {
                return  postList;
            }
            for (Document item : results) {
                PostStatus post = convertDocumentToPostedStatus(item);
                postList.add(post);
            }
            return postList;
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch(APPBadRequestException ae) {
            ae.printStackTrace();
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    public Object create(HttpHeaders headers, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            PostStatus status = this.convertJsonToPostStatus(json);

            CheckAuthentication.check(headers, status.getUserId());

            postedStatusCollection.insertOne(convertPostStatusToDocument(status));

            userLevelService.addScore(3, status.getUserId());  // add 3 score to userScore
            return status;
        } catch(JsonProcessingException e) {
            e.printStackTrace();
            throw new APPBadRequestException(55, "Json is invalid format");
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (APPBadRequestException b) {
            b.printStackTrace();
            throw b;
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    public Object update(HttpHeaders headers, String id, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            CheckAuthentication.check(headers, json.getString("userId"));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("textValue"))
                doc.append("textValue",json.getString("textValue"));
            if (json.has("date"))
                doc.append("date",json.getString("date"));
            if (json.has("pictures")) {
                BasicDBList pictures = new BasicDBList();
                JSONArray picturesJsonArray = json.getJSONArray("pictures");
                for(int i = 0 ; i <picturesJsonArray.length() ; i++) {
                    Document item = new Document();
                    item.append("url", (picturesJsonArray.get(i)));
                    pictures.add(item);
                }
                doc.append("pictures", pictures);
            }

            Document set = new Document("$set", doc);
            postedStatusCollection.updateOne(query,set);
            return request;

        } catch(JSONException je) {
            je.printStackTrace();
            throw new APPBadRequestException(55, "Json is invalid format");
        } catch(JsonProcessingException jpe) {
            jpe.printStackTrace();
            throw new APPBadRequestException(55, "Json is invalid format");
        } catch (APPBadRequestException b) {
            b.printStackTrace();
            throw b;
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }


    public Object delete(String id) {
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            postedStatusCollection.deleteOne(query);

            return new JSONObject();
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    private Document convertPostStatusToDocument(PostStatus status) {
        Document doc = new Document("userId", status.getUserId())
                .append("textValue", status.getTextValue())
                .append("date", status.getDate())
                .append("commentCount", status.getCommentCount());

        BasicDBList pictures = new BasicDBList();
        for(String picUrl: status.getPictures()) {
            Document item = new Document();
            item.append("url", picUrl);
            pictures.add(item);
        }
        doc.append("pictures", pictures);
        return doc;
    }

    private PostStatus convertDocumentToPostedStatus(Document item) {
        List<Document> pictures = (List<Document>)item.get("pictures");
        List<String> picturesList = new ArrayList();
        for(Document pic : pictures) {
            picturesList.add(pic.getString("url"));
        }

        PostStatus status = new PostStatus(item.getString("userId"),
                item.getString("textValue"),
                picturesList, item.getDate("date"));
        status.setCommentCount(item.getInteger("commentCount"));
        status.setId(item.getObjectId("_id").toString());
        return status;
    }

    private PostStatus convertJsonToPostStatus(JSONObject item) {
        JSONArray picturesArr = item.getJSONArray("pictures");
        List<String> pictures = new ArrayList();
        for(Object picUrl : picturesArr) {
            pictures.add((String)picUrl);
        }

        PostStatus status = new PostStatus(item.getString("userId"),
                item.getString("textValue"),
                pictures,
                new Date());
        return status;
    }

    public Object createComment(HttpHeaders headers, String postId, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            PostComment comment = this.convertJsonToPostComment(postId, json);

            CheckAuthentication.check(headers, comment.getUserId());

            commentCollection.insertOne(convertPostCommentToDocument(comment));
            userLevelService.addScore(1, comment.getUserId());  // add 1 score to userScore
            increaseOrDecreaseCommentCount(postId, true);
            addNotificationWhenCommentPosted(comment);
            return comment;
        } catch (JsonProcessingException jpe) {
            jpe.printStackTrace();
            throw new APPBadRequestException(55, "Json is invalid format");
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    public boolean increaseOrDecreaseCommentCount(String postId, boolean isIncrease) {
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(postId));

            Document result = postedStatusCollection.find(query).first();
            PostStatus postStatus = convertDocumentToPostedStatus(result);

            Document doc = new Document();
            doc.append("commentCount", isIncrease ? postStatus.getCommentCount()+1 : postStatus.getCommentCount()-1);
            Document set = new Document("$set", doc);
            postedStatusCollection.updateOne(query,set);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    private boolean addNotificationWhenCommentPosted(PostComment comment) {
        try {
            ArrayList<PostComment> allCommentsUnderPost = getAllComments(comment.getPostId());
            PostStatus postStatus = queryPostStatus(comment.getPostId());
            notificationUtil.addNotificationWhenCommentIsAdded(comment, allCommentsUnderPost, postStatus);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object deleteComment(HttpHeaders headers, String commentId) {
        try {
            PostComment comment = getComment(commentId);
            if(comment == null) {
                throw new APPBadRequestException(34, "This comment is already deleted");
            }

            CheckAuthentication.check(headers, comment.getUserId());
            increaseOrDecreaseCommentCount(comment.getPostId(), false);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(commentId));

            commentCollection.deleteOne(query);

            return new JSONObject();
        } catch (APPUnauthorizedException a) {
            a.printStackTrace();
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    private PostComment getComment(String commentId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(commentId));
        Document item = commentCollection.find(query).first();

        if (item == null) {
            return null;
        }

        PostComment comment = convertDocumentToPostComment(item);
        return comment;
    }

    public ArrayList<PostComment> getAllComments(String postId) {

        BasicDBObject query = new BasicDBObject();
        query.put("postId", postId);

        FindIterable<Document> items = commentCollection.find(query).sort(new BasicDBObject("date", 1));
        ArrayList<PostComment> result = new ArrayList();
        for(Document item : items) {
            result.add(convertDocumentToPostComment(item));
        }

        return result;
    }

    private PostComment convertJsonToPostComment(String postId, JSONObject item) {
        PostComment status = new PostComment(postId, item.getString("content"),new Date(), item.getString("userId") );
        return status;
    }

    private Document convertPostCommentToDocument(PostComment comment) {
        Document doc = new Document("postId", comment.getPostId())
                .append("content", comment.getContent())
                .append("date", comment.getDate())
                .append("userId", comment.getUserId());
        return doc;
    }

    private PostComment convertDocumentToPostComment(Document item) {
        PostComment post = new PostComment(item.getString("postId"), item.getString("content"), item.getDate("date"), item.getString("userId"));
        post.setId(item.getObjectId("_id").toString());
        return post;
    }


}
