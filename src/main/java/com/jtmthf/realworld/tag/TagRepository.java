package com.jtmthf.realworld.tag;

import com.jtmthf.realworld.repository.NaturalRepository;

import java.util.List;

public interface TagRepository extends NaturalRepository<Tag, Long, String> {

    List<Tag> findByNameIn(List<String> names);
}
