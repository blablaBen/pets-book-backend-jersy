package com.app.server.util;

import com.app.server.http.exceptions.APPUnauthorizedException;

import javax.ws.rs.core.HttpHeaders;
import java.util.List;

public class CheckAuthentication {

    static public void check(HttpHeaders headers, String userId) throws Exception {
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70, "No Authorization Headers");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        if (userId.compareTo(clearToken) != 0) {
            throw new APPUnauthorizedException(71, "Invalid token. Please try getting a new token");
        }
    }
}
