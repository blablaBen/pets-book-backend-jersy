package com.app.server.http;


import com.app.server.http.utils.APPResponse;
import com.app.server.models.ForgetPassword;
import com.app.server.services.ForgetPasswordService;
import com.app.server.services.UserLevelService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("forgetPassword")
public class ForgetPasswordHttpService {

    ForgetPasswordService forgetInterface;
    UserLevelService userLevelService;

    public ForgetPasswordHttpService() {

        this.forgetInterface = ForgetPasswordService.getInstance();
        this.userLevelService = UserLevelService.getInstance();
    }

    // User requests the system to send the email when they forget password with their registered email address
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse forget(Object request) {

        return new APPResponse(forgetInterface.create(request));
    }

    // User requests the system to send the email when they forget password with their registered email address
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse confirmWithToken(@QueryParam("token") String token) {
        ForgetPassword forget = forgetInterface.getByToken(token);
        return new APPResponse(forget != null);
    }


    //Reset the password with token and the new password
    @Path("reset")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse resetPassword(Object request) {
        forgetInterface.resetPassword(request);
        return new APPResponse();
    }

//    @Path("/score")
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    public APPResponse testUserScore( @QueryParam("userId")String userId, @QueryParam("add") int add) {
//        userLevelService.addScore(add, userId);
//        return new APPResponse(true);
//    }

}
