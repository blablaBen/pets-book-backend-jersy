package com.app.server.services;

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
import java.util.List;

public class FeedService {
    private static FeedService self;
    private ObjectWriter ow;
    private MongoCollection<Document> postedStatusCollection = null;
    private MongoCollection<Document> commentCollection = null;

    private FeedService() {
        this.postedStatusCollection = MongoPool.getInstance().getCollection("poststatus");
        this.commentCollection = MongoPool.getInstance().getCollection("comment");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static FeedService getInstance(){
        if (self == null)
            self = new FeedService();
        return self;
    }

    public PostStatus getOne(HttpHeaders headers, String id, String userId) {
        try {
            CheckAuthentication.check(headers, userId);

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            Document item = postedStatusCollection.find(query).first();

            if (item == null) {
                return null;
            }

            PostStatus status = convertDocumentToPostedStatus(item);
            return status;
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    public ArrayList<PostStatus> getAll(HttpHeaders headers, String userId) {
        try {
            CheckAuthentication.check(headers, userId);
            ArrayList<PostStatus> postList = new ArrayList<PostStatus>();

            FindIterable<Document> results = postedStatusCollection.find();
            if (results == null) {
                return  postList;
            }
            for (Document item : results) {
                PostStatus post = convertDocumentToPostedStatus(item);
                postList.add(post);
            }
            return postList;
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
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
            return status;
        } catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
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

        } catch(JSONException e) {
            System.out.println("Failed to update a document");
            return null;

        } catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
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
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
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
                picturesList,
                item.getString("date"));
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
                item.getString("date"));
        return status;
    }

    public Object createComment(HttpHeaders headers, String postId, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            PostComment comment = this.convertJsonToPostComment(postId, json);

            CheckAuthentication.check(headers, comment.getUserId());

            commentCollection.insertOne(convertPostCommentToDocument(comment));
            return comment;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    public Object deleteComment(HttpHeaders headers, String commentId) {
        try {
            PostComment comment = getComment(commentId);
            CheckAuthentication.check(headers, comment.getUserId());

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(commentId));

            commentCollection.deleteOne(query);

            return new JSONObject();
        } catch (APPUnauthorizedException a) {
            throw new APPUnauthorizedException(34, a.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update a document");
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

        FindIterable<Document> items = commentCollection.find(query);
        ArrayList<PostComment> result = new ArrayList();
        for(Document item : items) {
            result.add(convertDocumentToPostComment(item));
        }

        return result;
    }

    private PostComment convertJsonToPostComment(String postId, JSONObject item) {
        PostComment status = new PostComment(postId, item.getString("content"), item.getString("time"), item.getString("userId") );
        return status;
    }

    private Document convertPostCommentToDocument(PostComment comment) {
        Document doc = new Document("postId", comment.getPostId())
                .append("content", comment.getContent())
                .append("time", comment.getTime())
                .append("userId", comment.getUserId());
        return doc;
    }

    private PostComment convertDocumentToPostComment(Document item) {
        PostComment post = new PostComment(item.getString("postId"), item.getString("content"), item.getString("time"), item.getString("userId"));
        post.setId(item.getObjectId("_id").toString());
        return post;
    }


}
