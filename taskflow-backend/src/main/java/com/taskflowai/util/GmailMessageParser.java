package com.taskflowai.util;

import com.taskflowai.dto.gmail.GmailMessageDetail;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

public final class GmailMessageParser {

    private GmailMessageParser() {
    }

    public static String extractHeader(GmailMessageDetail message, String headerName) {
        if (message.getPayload() == null || message.getPayload().getHeaders() == null) {
            return null;
        }
        return message.getPayload().getHeaders().stream()
                .filter(h -> headerName.equalsIgnoreCase(h.getName()))
                .map(GmailMessageDetail.Header::getValue)
                .findFirst()
                .orElse(null);
    }

    public static Instant extractReceivedAt(GmailMessageDetail message) {
        if (message.getInternalDate() != null) {
            try {
                return Instant.ofEpochMilli(Long.parseLong(message.getInternalDate()));
            } catch (NumberFormatException ignored) {
                // fall through
            }
        }
        String dateHeader = extractHeader(message, "Date");
        if (dateHeader != null) {
            try {
                return Instant.parse(dateHeader);
            } catch (Exception ignored) {
                return Instant.now();
            }
        }
        return Instant.now();
    }

    public static String extractBody(GmailMessageDetail message) {
        if (message.getPayload() == null) {
            return message.getSnippet();
        }
        String body = extractBodyFromPayload(message.getPayload());
        if (body != null && !body.isBlank()) {
            return body.trim();
        }
        return message.getSnippet();
    }

    private static String extractBodyFromPayload(GmailMessageDetail.Payload payload) {
        if (payload.getParts() != null && !payload.getParts().isEmpty()) {
            for (GmailMessageDetail.Part part : payload.getParts()) {
                String text = extractBodyFromPart(part);
                if (text != null && !text.isBlank()) {
                    return text;
                }
            }
        }
        return decodeBodyData(payload.getBody());
    }

    private static String extractBodyFromPart(GmailMessageDetail.Part part) {
        if ("text/plain".equalsIgnoreCase(part.getMimeType())) {
            return decodeBodyData(part.getBody());
        }
        if (part.getParts() != null) {
            for (GmailMessageDetail.Part nested : part.getParts()) {
                String text = extractBodyFromPart(nested);
                if (text != null && !text.isBlank()) {
                    return text;
                }
            }
        }
        if ("text/html".equalsIgnoreCase(part.getMimeType())) {
            return stripHtml(decodeBodyData(part.getBody()));
        }
        return null;
    }

    private static String decodeBodyData(GmailMessageDetail.Body body) {
        if (body == null || body.getData() == null) {
            return null;
        }
        byte[] decoded = Base64.getUrlDecoder().decode(body.getData());
        return new String(decoded, StandardCharsets.UTF_8);
    }

    private static String stripHtml(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
    }
}
