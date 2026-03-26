package com.example.miniproject02.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.miniproject02.data.entity.Order;

@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Order order);

    @Update
    void update(Order order);

    @Query("SELECT * FROM orders WHERE id = :id")
    Order findById(long id);

    @Query("SELECT * FROM orders WHERE userId = :userId AND status = 'PENDING' LIMIT 1")
    Order getPendingOrderForUser(long userId);
}
