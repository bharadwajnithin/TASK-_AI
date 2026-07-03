package com.taskflowai.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WhatsAppChatParserTest {

    @Test
    void parse_bracketedFormat_extractsMessages() {
        String content = """
                [6/4/26, 10:30:15 AM] John Doe: Please update the login page
                [6/4/26, 10:31:00 AM] Jane Smith: Before Friday please
                """;

        WhatsAppChatParser.ParsedChat parsed = WhatsAppChatParser.parse(content, "Client chat");

        assertEquals("Client chat", parsed.getTitle());
        assertEquals(2, parsed.getMessageCount());
        assertEquals(2, parsed.getParticipants().size());
        assertTrue(parsed.getFormattedContent().contains("John Doe"));
        assertTrue(parsed.getFormattedContent().contains("Jane Smith"));
    }

    @Test
    void parse_dashedFormat_extractsMessages() {
        String content = """
                6/4/26, 10:30 AM - John: Hello team
                6/4/26, 10:31 AM - Jane: Send deployment link
                """;

        WhatsAppChatParser.ParsedChat parsed = WhatsAppChatParser.parse(content, null);

        assertEquals(2, parsed.getMessageCount());
        assertTrue(parsed.getTitle().contains("WhatsApp"));
    }
}
