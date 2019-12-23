package com.jtmthf.realworld.tag;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public List<Tag> createTags(List<String> tagNames) {
        List<Tag> found = tagRepository.findByNameIn(tagNames);

        Set<Tag> tagSet = new HashSet<>(found);
        tagNames.forEach(name -> tagSet.add(new Tag(name)));

        return tagRepository.saveAll(tagSet);
    }
}
