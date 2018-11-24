package com.app.server.http;

import com.app.server.http.exceptions.APPExceptionInfo;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.services.MessagingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("messages")
public class MessagingHttpService {
    private MessagingService service;
    private ObjectWriter ow;


    public MessagingHttpService() {
        service = MessagingService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    //Getting all chat rooms.
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getAllChatRooms(@Context HttpHeaders headers, @QueryParam("userId") String userId, @QueryParam("userType") String userType) {
        if(userId == null || userType == null) {
            APPResponse errorResponse = new APPResponse();
            errorResponse.success = false;
            errorResponse.data = "userId and userType are required";
            return errorResponse;
        }
        return new APPResponse(service.getAllChatRooms(headers, userId, Integer.parseInt(userType)));
    }


    // Creating a chat room
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse createChat(Object request, @Context HttpHeaders headers) {
        return new APPResponse(service.createRoom(headers, request));
    }

    //Getting all message in a chat room.
    @GET
    @Path("{roomId}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getAllChatMessage(@Context HttpHeaders headers, @PathParam("roomId") String roomId, @QueryParam("userType") String userType, @QueryParam("pageSize") String pageSize
            , @QueryParam("page") String page) {
        return new APPResponse(service.getAllChatMessage(headers, roomId, userType, pageSize, page));
    }

    //Post a message in a chat room.
    @POST
    @Path("{roomId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse addNewMessage(@Context HttpHeaders headers, @PathParam("roomId") String roomId, Object request) {
        return new APPResponse(service.createMessage(headers, request));
    }
}
