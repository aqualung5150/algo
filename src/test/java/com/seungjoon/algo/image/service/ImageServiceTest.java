package com.seungjoon.algo.image.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

public class ImageServiceTest {

    @Test
    void markdownImageParse() {
        //given
        String content = """
                테스트 content 시작
                
                ![](https://algo.rockaria.store/images/aaaa.png)
                
                테스트 content 중간
                
                ![](https://algo.rockaria.store/images/bbbb.png)
                
                테스트 content 종료
                """;
        //when
        Set<String> paths = extractImageIds(content);

        //then
        assertThat(paths).hasSize(2);
        assertThat(paths).containsExactlyInAnyOrder("aaaa.png", "bbbb.png");
    }

    private Set<String> extractImageIds(String markdownContent) {
        Pattern pattern = Pattern.compile("!\\[.*?\\]\\((.*?)\\)");
        Matcher matcher = pattern.matcher(markdownContent);

        Set<String> result = new HashSet<>();
        while (matcher.find()) {
            String url = matcher.group(1);

            String[] split = url.split("/");
            result.add(split[split.length - 1]);
        }
        return result;
    }
}
