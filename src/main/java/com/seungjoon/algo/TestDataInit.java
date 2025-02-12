package com.seungjoon.algo;

import com.seungjoon.algo.subject.domain.Tag;
import com.seungjoon.algo.subject.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

//TEST
@RequiredArgsConstructor
public class TestDataInit {

    private final TagRepository tagRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        tagRepository.save(new Tag("dp"));
        tagRepository.save(new Tag("dfs"));
        tagRepository.save(new Tag("bfs"));
    }
}
