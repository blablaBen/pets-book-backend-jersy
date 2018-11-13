package com.app.server.http;

import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.services.FeedService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("newFeeds")
public class FeedHttpService {
    private FeedService service;
    private ObjectWriter ow;


    public FeedHttpService() {
        service = FeedService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }


    @OPTIONS
    @PermitAll
    public Response optionsById() {
        return Response.ok().build();
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll(@Context HttpHeaders headers) {

        return new APPResponse(service.getAll(headers));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id, @Context HttpHeaders headers) {

        return new APPResponse(service.getOne(headers,id));
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(@Context HttpHeaders headers,Object request) {
        return new APPResponse(service.create(headers,request));
    }

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse update(@Context HttpHeaders headers, @PathParam("id") String id, Object request){

        return new APPResponse(service.update(headers, id,request));

    }

    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {

        return new APPResponse(service.delete(id));
    }


    @GET
    @Path("{postId}/comments")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getComments(@PathParam("postId") String postId) {

        return new APPResponse(service.getAllComments(postId));
    }

    @POST
    @Path("{postId}/comments")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createComment(@Context HttpHeaders headers,@PathParam("postId") String postId, Object request) {
        return new APPResponse(service.createComment(headers, postId, request));
    }

    @DELETE
    @Path("{postId}/comments/{commentId}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse  deleteComment(@Context HttpHeaders headers,@PathParam("commentId") String commentId) {
        return new APPResponse(service.deleteComment(headers, commentId));
    }

}
