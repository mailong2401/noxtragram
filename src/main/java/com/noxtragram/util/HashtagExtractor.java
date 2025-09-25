package com.noxtragram.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashtagExtractor {

  private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");

  public static List<String> extractHashtags(String text) {
    List<String> hashtags = new ArrayList<>();

    if (text == null || text.trim().isEmpty()) {
      return hashtags;
    }

    Matcher matcher = HASHTAG_PATTERN.matcher(text);
    while (matcher.find()) {
      String hashtag = matcher.group(1);
      if (!hashtag.isEmpty()) {
        hashtags.add(hashtag.toLowerCase());
      }
    }

    return hashtags;
  }

  public static String cleanHashtagName(String name) {
    if (name == null) {
      return "";
    }
    return name.startsWith("#") ? name.substring(1).trim().toLowerCase() : name.trim().toLowerCase();
  }
}
