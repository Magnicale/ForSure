package ru.itis.timecapsule.service;

import ru.itis.timecapsule.model.Category;
import ru.itis.timecapsule.model.Tag;
import ru.itis.timecapsule.repository.CategoryRepository;
import ru.itis.timecapsule.repository.TagRepository;

import java.sql.SQLException;
import java.util.List;

public class AdminService {
    private final CategoryRepository categories;
    private final TagRepository tags;

    public AdminService(CategoryRepository categories, TagRepository tags) {
        this.categories = categories;
        this.tags = tags;
    }

    public List<Category> getCategories() throws SQLException { return categories.findAll(); }
    public List<Tag> getTags() throws SQLException { return tags.findAll(); }

    public void createCategory(String name) throws SQLException {
        Category category = validated(name);
        categories.save(category);
    }

    public void updateCategory(long id, String name) throws SQLException {
        Category category = validated(name);
        category.setId(id);
        categories.update(category);
    }

    public void deleteCategory(long id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Некорректная категория.");
        categories.delete(id);
    }

    private Category validated(String name) {
        String value = name == null ? "" : name.trim();
        if (value.isEmpty() || value.length() > 60) throw new IllegalArgumentException("Название категории должно быть от 1 до 60 символов.");
        Category category = new Category();
        category.setName(value);
        return category;
    }
}
