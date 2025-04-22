package com.seungjoon.algo.image.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtil {

    public static Set<String> extractImageIdsFromMarkdown(String markdownContent) {
        Pattern pattern = Pattern.compile("!\\[.*?\\]\\((.*?)\\)");
        Matcher matcher = pattern.matcher(markdownContent);

        Set<String> result = new HashSet<>();
        while (matcher.find()) {
            String url = matcher.group(1);

            //Windows backslash path
//            String[] split = url.split("\\\\");

            String[] split = url.split("/");
            result.add(split[split.length - 1]);
        }
        return result;
    }
}
