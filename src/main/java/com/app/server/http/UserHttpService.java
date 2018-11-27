package com.app.server.http;


import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.services.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("user")
public class UserHttpService {

    UserService userService;

    public UserHttpService() {
        this.userService = UserService.getInstance();
    }

    //create a user
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse postUser(Object obj) {

        return new APPResponse(userService.create(obj).getId());

    }

    //Getting a user information.
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOneUser(@PathParam("id") String id) {

        return new APPResponse(userService.getOne(id));

    }


    // Editing a profileName and portraitUrl of a user
    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updateUserInfo(@Context HttpHeaders headers, @PathParam("id") String id, Object request) {

        return new APPResponse(userService.update(headers, id, request));

    }


    // "Get the list of user by search parameters  by asc profileName  or userType
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getUsers(@QueryParam("userType") Integer userType, @QueryParam("profileName") String profileName) {

        return new APPResponse(userService.getAll(userType, profileName));
    }

    @GET
    @Path("check")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse checkExist(@QueryParam("email") String email) {
        return new APPResponse(userService.getByEmail(email) != null);
    }


    //create a pet
    @POST
    @Path("{userId}/pets")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse postPet(@Context HttpHeaders headers, Object obj, @PathParam("userId") String userId) {
        return new APPResponse(userService.createPetProfile(headers, obj, userId).getId());
    }

    //Getting all pets.
    @GET
    @Path("{userId}/pets")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getAllPets(@Context HttpHeaders headers, @PathParam("userId") String userId) {
        return new APPResponse(userService.getAllPets(headers, userId));
    }


    // Editing a pet detail and portraiUrl
    @PATCH
    @Path("{userId}/pets/{petId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updatePetInfo(@Context HttpHeaders headers,@PathParam("petId") String petId, @PathParam("userId") String userId,  Object request) {
        return new APPResponse(userService.updatePetProfile(headers, petId,userId, request));
    }

    // Deleting a pet
    @DELETE
    @Path("{userId}/pets/{petId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse deletePet(@Context HttpHeaders headers, @PathParam("petId") String petId, @PathParam("userId") String userId) {
        return new APPResponse(userService.deletePetProfile(headers, petId, userId));
    }

}
