package com.app.server.enumeration;


public enum UserLevelEnum {

    LEVEL_1(1, "bronze", 100, 999), LEVEL_2(2, "silver", 1000, 1999), LEVEL_3(3, "gold", 2000, 100000000);

    private int value;
    private String name;
    private int lowScore;
    private int highScore;


    UserLevelEnum(int newValue, String name, int lowscore, int highScore) {
        value = newValue;
        name = name;
        lowScore = lowscore;
        highScore = highScore;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public int getLowScore() {
        return lowScore;
    }

    public int getHighScore() {
        return highScore;
    }

    public static UserLevelEnum getNextLevel(int level) {
        if (level == 1) return LEVEL_2;
        else if (level == 2) return LEVEL_3;
        else return LEVEL_3;
    }

    public static UserLevelEnum valueOf(int level) {
        for (UserLevelEnum b : UserLevelEnum.values()) {
            if (b.value == level) {
                return b;
            }
        }
        return null;
    }
}
