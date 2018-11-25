package com.app.server.services;


import com.app.server.enumeration.UserLevelEnum;
import com.app.server.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class UserLevelService {

    private static UserLevelService self;

    private ObjectWriter ow;

    private UserInterface userInterface;


    private UserLevelService() {
        this.userInterface = UserInterface.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static UserLevelService getInstance() {
        if (self == null)
            self = new UserLevelService();
        return self;
    }

    public void addScore(int add, String userId) {
        User user = userInterface.getOne(userId);
        int scoreBefore = user.getUserScore();
        userInterface.updateScore(userId, scoreBefore + add);
        user.setUserScore(scoreBefore + add);
        decideuUserLevel(user);
    }

    public void decideuUserLevel(User user) {
        int curLevel = user.getUserLevel();
        UserLevelEnum userLevel = UserLevelEnum.valueOf(curLevel);
        if (user.getUserScore() > userLevel.getHighScore()) {
            UserLevelEnum nextLevel = UserLevelEnum.getNextLevel(curLevel);
            userInterface.updateLevel(user.getId(), nextLevel.getValue());
        }
    }


}
