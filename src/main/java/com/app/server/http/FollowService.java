package com.app.server.http;


import com.app.server.http.utils.APPResponse;
import com.app.server.services.FollowInterface;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("user/{userId}/follow")
public class FollowService {

    FollowInterface followInterface;

    public FollowService() {
        this.followInterface = FollowInterface.getInstance();
    }

    //GET Follow - All
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getFollow(@Context HttpHeaders headers, @PathParam("userId") String userId, @QueryParam("type") @NotNull Integer type, @QueryParam("justCount") boolean justCount) {
        ArrayList<String> follows = new ArrayList<>();
        follows = followInterface.getAll(headers, userId, type);
        return new APPResponse((follows));
    }


    //add a Follow
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse addFollow(@Context HttpHeaders headers, @PathParam("userId") String userId, Object request) {
        followInterface.addFollow(headers, userId, request);
        return new APPResponse();

    }


    //DELETE a Follow
    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Object deleteFollowing(@Context HttpHeaders headers, @PathParam("userId") String userId, @PathParam("id") String id) {

        followInterface.deleteFollowing(headers, userId, id);
        return "success";

    }

}
