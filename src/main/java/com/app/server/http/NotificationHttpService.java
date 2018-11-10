package com.app.server.http;

import com.app.server.http.exceptions.APPExceptionInfo;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.services.MessagingService;
import com.app.server.services.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("notification")
public class NotificationHttpService {
    private NotificationService service;
    private ObjectWriter ow;


    public NotificationHttpService() {
        service = NotificationService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }


    //Getting all notification.
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getAllChatRooms(@Context HttpHeaders headers, @QueryParam("userId") String userId) {
        if(userId == null ) {
            APPResponse errorResponse = new APPResponse();
            errorResponse.success = false;
            errorResponse.data = "userId is required";
            return errorResponse;
        }
        return new APPResponse(service.getAllNotification(headers, userId));
    }
}