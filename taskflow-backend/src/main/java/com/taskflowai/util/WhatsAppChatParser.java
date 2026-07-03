package com.taskflowai.util;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WhatsAppChatParser {

  private static final Pattern BRACKETED =
      Pattern.compile(
          "^\\[(\\d{1,2}/\\d{1,2}/\\d{2,4},\\s+\\d{1,2}:\\d{2}(?::\\d{2})?\\s*(?:AM|PM)?)\\]\\s*([^:]+):\\s?(.*)$",
          Pattern.CASE_INSENSITIVE);

  private static final Pattern DASHED =
      Pattern.compile(
          "^(\\d{1,2}/\\d{1,2}/\\d{2,4},\\s+\\d{1,2}:\\d{2}(?::\\d{2})?\\s*(?:AM|PM)?)\\s*-\\s*([^:]+):\\s?(.*)$",
          Pattern.CASE_INSENSITIVE);

  private WhatsAppChatParser() {}

  @Data
  @Builder
  public static class ParsedChat {
    private String title;
    private String formattedContent;
    private int messageCount;
    private List<String> participants;
  }

  public static ParsedChat parse(String rawContent, String titleHint) {
    if (!StringUtils.hasText(rawContent)) {
      throw new IllegalArgumentException("Chat content is empty");
    }

    String normalized = rawContent.replace("\r\n", "\n").replace('\r', '\n').trim();
    String[] lines = normalized.split("\n", -1);

    List<String> formattedLines = new ArrayList<>();
    Set<String> participants = new LinkedHashSet<>();
    int messageCount = 0;

    String currentTimestamp = null;
    String currentSender = null;
    StringBuilder currentMessage = new StringBuilder();

    for (String line : lines) {
      Matcher bracketed = BRACKETED.matcher(line);
      Matcher dashed = DASHED.matcher(line);

      if (bracketed.matches() || dashed.matches()) {
        if (currentSender != null && !currentMessage.isEmpty()) {
          flushMessage(formattedLines, currentTimestamp, currentSender, currentMessage);
          messageCount++;
        }

        Matcher matcher = bracketed.matches() ? bracketed : dashed;
        currentTimestamp = matcher.group(1).trim();
        currentSender = matcher.group(2).trim();
        currentMessage = new StringBuilder(matcher.group(3).trim());
        participants.add(currentSender);
      } else if (currentSender != null) {
        if (!currentMessage.isEmpty()) {
          currentMessage.append('\n');
        }
        currentMessage.append(line);
      } else if (formattedLines.isEmpty() && StringUtils.hasText(line)) {
        formattedLines.add(line.trim());
      }
    }

    if (currentSender != null && !currentMessage.isEmpty()) {
      flushMessage(formattedLines, currentTimestamp, currentSender, currentMessage);
      messageCount++;
    }

    String title = StringUtils.hasText(titleHint)
        ? titleHint.trim()
        : deriveTitle(lines, participants);

    String formattedContent = String.join("\n", formattedLines);

    if (messageCount == 0) {
      formattedContent = normalized;
      messageCount = (int) java.util.Arrays.stream(lines).filter(StringUtils::hasText).count();
    }

    return ParsedChat.builder()
        .title(title)
        .formattedContent(formattedContent)
        .messageCount(messageCount)
        .participants(new ArrayList<>(participants))
        .build();
  }

  private static void flushMessage(
      List<String> formattedLines,
      String timestamp,
      String sender,
      StringBuilder message) {
    if (!StringUtils.hasText(sender)) {
      return;
    }
    String body = message.toString().trim();
    if (!StringUtils.hasText(body)) {
      return;
    }
    formattedLines.add("[" + timestamp + "] " + sender + ": " + body);
  }

  private static String deriveTitle(String[] lines, Set<String> participants) {
    for (String line : lines) {
      String trimmed = line.trim();
      if (trimmed.startsWith("Messages and calls are end-to-end encrypted")) {
        continue;
      }
      if (trimmed.contains(" created group ")) {
        return trimmed.length() > 80 ? trimmed.substring(0, 80) : trimmed;
      }
    }

    if (!participants.isEmpty()) {
      List<String> names = new ArrayList<>(participants);
      if (names.size() == 1) {
        return "WhatsApp chat with " + names.get(0);
      }
      return "WhatsApp group (" + names.size() + " participants)";
    }

    return "WhatsApp chat import";
  }
}
