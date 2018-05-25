package com.digitala.galoshes.category;

import java.util.Date;
import java.util.List;

import com.digitala.galoshes.rest.Category;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.example.flyway.db.h2.tables.Widgets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired private CategoryRepository categoryRepository;

    public CategoryServiceImpl() {
        System.out.println("JIAG:Instance being created!!!!");
    }


	@Override
	public List<String> getCategories() {
		return categoryRepository.getCategoryNames();
	}

}