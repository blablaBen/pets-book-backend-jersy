package com.app.server.util;

import com.app.server.http.exceptions.APPUnauthorizedException;

import javax.ws.rs.core.HttpHeaders;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CheckAuthentication {

    static public void check(HttpHeaders headers, String userId) throws Exception {
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70, "No Authorization Headers");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        String[] parts = clearToken.split("\\|");
        if (parts.length != 2) {
            throw new APPUnauthorizedException(71, "Invalid token. Please try getting a new token");
        }
        String userIdInToken = parts[0];
        String time = parts[1];
        if (userId.compareTo(userIdInToken) != 0) {
            throw new APPUnauthorizedException(71, "Invalid token. Please try getting a new token");
        }
        Date date = convertStrToDate(time);
        Date validDate = getValidDate();
        if (date.before(validDate)) {
            throw new APPUnauthorizedException(71, "Invalid token. Please try getting a new token");
        }
    }

    static public void onlyCheckAuthenthicationProvided(HttpHeaders headers) throws Exception {
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70, "No Authorization Headers");
    }

    static public String generateToken(String userId) {
        String crypt = "";
        try {
            crypt = APPCrypt.encrypt(userId + "|" + convertDateToString(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return crypt;
    }


    static public String convertDateToString(Date date) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = formatter.format(date);
        return s;
    }

    static public Date convertStrToDate(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return date;
    }

    // 3 days before now
    static public Date getValidDate() {
        Date now = new Date();
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -3);
        Date validDate = cal.getTime();
        return validDate;
    }



}
