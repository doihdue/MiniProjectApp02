package com.example.miniproject02.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.miniproject02.data.entity.OrderDetail;

import java.util.List;

@Dao
public interface OrderDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(OrderDetail orderDetail);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    List<OrderDetail> getByOrder(long orderId);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId AND productId = :productId LIMIT 1")
    OrderDetail findByOrderAndProduct(long orderId, long productId);

    @Update
    void update(OrderDetail orderDetail);
}
