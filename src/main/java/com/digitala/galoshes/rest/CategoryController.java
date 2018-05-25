package com.digitala.galoshes.rest;

import java.util.List;

import com.digitala.galoshes.category.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {

    @Autowired CategoryService categoryService;
    

    //(@RequestParam(value="name", defaultValue="World") String name)

    @RequestMapping("/categories")
    public List<String> categories() {
        return categoryService.getCategories();
    }

}