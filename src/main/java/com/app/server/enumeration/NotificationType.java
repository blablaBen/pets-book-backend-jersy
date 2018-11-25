package com.app.server.enumeration;

public enum NotificationType
{
    NEW_COMMENT(1), NEW_FOLLOWER(2), NEW_MESSAGE(3);

    private final int value;

    NotificationType(final int newValue) {
        value = newValue;
    }

    public String getValue() { return value+""; }
}
