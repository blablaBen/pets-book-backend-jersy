package com.app.server.http;


import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.services.UserInterface;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("user")
public class UserService {

    UserInterface userInterface = new UserInterface();

    //create a user
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse postUser(Object obj) {

        return new APPResponse(userInterface.create(obj).getId());

    }

    //Getting a user information.
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOneUser(@PathParam("id") String id) {

        return new APPResponse(userInterface.getOne(id));

    }


    // Editing a profileName and portraitUrl of a user
    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updateUserInfo(@PathParam("id") String id, Object request) {

        return new APPResponse(userInterface.update(id, request));

    }


    // "Get the list of user by search parameters  by asc profileName  or userType
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getUsers(@QueryParam("userType") Integer userType, @QueryParam("profileName") String profileName) {

        return new APPResponse(userInterface.getAll(userType, profileName));
    }

    @GET
    @Path("check")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse checkExist(@QueryParam("email") String email) {
        return new APPResponse(userInterface.getByEmail(email) != null);
    }

}
