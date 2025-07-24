package com.dailyquest.dailyquest.type;

public enum HomeworkType {
    DAILY("일일반복"),
    WEEKLY("주간반복"),
    EVENT("이벤트"),
    ONCE("일회성");

    private final String displayName;

    HomeworkType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }


}
