package com.digitala.galoshes.category;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

	public List<String> getCategories();

}