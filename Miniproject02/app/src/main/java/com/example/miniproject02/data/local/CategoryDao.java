package com.example.miniproject02.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.miniproject02.data.entity.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Category category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Category> categories);

    @Query("SELECT * FROM categories")
    List<Category> getAll();

    @Query("SELECT COUNT(*) FROM categories")
    int count();

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    Category findByName(String name);
}
