package com.github.iitdevelopment;

public enum HttpCode {
    OK(200, "OK"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String description;

    HttpCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
