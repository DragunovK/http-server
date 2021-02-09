package com.github.iitdevelopment;

import java.util.Arrays;

public enum ContentType {
    PLAIN_TEXT("text/plain", "txt"),
    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("application/javascript", "js"),
    PNG("image/png", "png"),
    JPEG("image/jpeg", "jpg"),
    SVG("image/svg+xml", "svg");

    private final String type;
    private final String extension;

    ContentType(String type, String extension) {
        this.type = type;
        this.extension = extension;
    }

    public String getType() {
        return type;
    }

    public String getExtension() {
        return extension;
    }

    public static ContentType determine(String filename) {
        String ext = filename.substring(filename.lastIndexOf('.') + 1);

        return Arrays.stream(ContentType.values())
                .filter(e ->  e.getExtension().equalsIgnoreCase(ext))
                .findFirst()
                .orElse(PLAIN_TEXT);
    }
}
