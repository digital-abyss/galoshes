package com.digitala.galoshes.category;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository {

    public List<String> getCategoryNames();
}