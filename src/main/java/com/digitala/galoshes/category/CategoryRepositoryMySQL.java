package com.digitala.galoshes.category;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.example.flyway.db.h2.tables.Categories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CategoryRepositoryMySQL implements CategoryRepository {


    @Autowired DSLContext dslContext;

    @Override
    @Transactional
    public List<String> getCategoryNames() {
        Result<?> result =
        dslContext
           .select(Categories.CATEGORIES.NODE_NAME)
           .from(Categories.CATEGORIES)
           .fetch();
           List<String> results = result.getValues(Categories.CATEGORIES.NODE_NAME);

        System.out.println("result = " + results );

        return results;
    }
}