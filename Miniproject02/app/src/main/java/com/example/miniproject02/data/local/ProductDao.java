package com.example.miniproject02.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.miniproject02.data.entity.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Product> products);

    @Query("SELECT * FROM products")
    List<Product> getAll();

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    List<Product> getByCategory(long categoryId);

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    Product findById(long id);

    @Query("SELECT * FROM products WHERE name = :name LIMIT 1")
    Product findByName(String name);

    @Query("SELECT COUNT(*) FROM products")
    int count();
}
