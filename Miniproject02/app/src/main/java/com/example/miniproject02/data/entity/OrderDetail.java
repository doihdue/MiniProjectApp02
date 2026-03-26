package com.example.miniproject02.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_details",
        foreignKeys = {
                @ForeignKey(entity = Order.class,
                        parentColumns = "id",
                        childColumns = "orderId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Product.class,
                        parentColumns = "id",
                        childColumns = "productId",
                        onDelete = ForeignKey.CASCADE)
        })
public class OrderDetail {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long orderId;
    public long productId;
    public int quantity;
}
