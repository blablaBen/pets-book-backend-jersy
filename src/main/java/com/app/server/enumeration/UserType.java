package com.app.server.enumeration;

public enum UserType
{
    PET_OWNER(1), VET(2);

    private final int value;

    UserType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
