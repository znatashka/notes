package ru.u26c4.logic.util;

public enum RedisKey {

    NOTE("note:"),
    HISTORY("history:");

    private String prefix;

    RedisKey(String prefix) {
        this.prefix = prefix;
    }

    public String prefix() {
        return prefix;
    }
}
