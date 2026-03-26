package com.example.miniproject02.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.miniproject02.data.entity.Category;
import com.example.miniproject02.data.entity.Order;
import com.example.miniproject02.data.entity.OrderDetail;
import com.example.miniproject02.data.entity.Product;
import com.example.miniproject02.data.entity.User;

@Database(entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "shopping_db")
                            .allowMainThreadQueries() // for simplicity in small demo
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
