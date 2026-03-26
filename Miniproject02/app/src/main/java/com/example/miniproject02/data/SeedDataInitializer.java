package com.example.miniproject02.data;

import android.content.Context;

import com.example.miniproject02.data.entity.Category;
import com.example.miniproject02.data.entity.Product;
import com.example.miniproject02.data.entity.User;
import com.example.miniproject02.data.local.AppDatabase;
import com.example.miniproject02.data.local.CategoryDao;
import com.example.miniproject02.data.local.ProductDao;
import com.example.miniproject02.data.local.UserDao;

public class SeedDataInitializer {

    public static void seed(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        UserDao userDao = db.userDao();
        CategoryDao categoryDao = db.categoryDao();
        ProductDao productDao = db.productDao();

        if (userDao.countUsers() == 0) {
            User u = new User();
            u.username = "admin";
            u.password = "123456";
            u.fullName = "Admin";
            userDao.insert(u);
        }

        if (categoryDao.count() == 0) {
            Category c1 = new Category();
            c1.name = "Điện thoại";
            categoryDao.insert(c1);

            Category c2 = new Category();
            c2.name = "Laptop";
            categoryDao.insert(c2);

            Category c3 = new Category();
            c3.name = "Phụ kiện";
            categoryDao.insert(c3);
        }

        Category phone = categoryDao.findByName("Điện thoại");
        Category laptop = categoryDao.findByName("Laptop");
        Category accessory = categoryDao.findByName("Phụ kiện");

        if (phone == null || laptop == null || accessory == null) {
            return;
        }

        insertProductIfMissing(productDao, "iPhone 15", "Chip A16, camera 48MP, man hinh dep", 20000000, phone.id);
        insertProductIfMissing(productDao, "iPhone 15 Pro", "Titanium nhe, hieu nang cao cap", 28000000, phone.id);
        insertProductIfMissing(productDao, "Samsung Galaxy S24", "Flagship Samsung, AI camera", 18000000, phone.id);
        insertProductIfMissing(productDao, "Xiaomi 14", "Hieu nang manh, gia canh tranh", 15900000, phone.id);
        insertProductIfMissing(productDao, "Google Pixel 9", "Android goc, camera tinh toan", 21900000, phone.id);

        insertProductIfMissing(productDao, "MacBook Pro M3", "Laptop chuyen nghiep cho dev va design", 40000000, laptop.id);
        insertProductIfMissing(productDao, "MacBook Air M3", "Mong nhe, pin lau, van phong cao cap", 29900000, laptop.id);
        insertProductIfMissing(productDao, "Dell XPS 13", "Man hinh dep, build kim loai", 32900000, laptop.id);
        insertProductIfMissing(productDao, "ASUS ROG Zephyrus", "Laptop gaming hieu nang cao", 36900000, laptop.id);

        insertProductIfMissing(productDao, "AirPods Pro", "Tai nghe chong on cao cap", 6200000, accessory.id);
        insertProductIfMissing(productDao, "Logitech MX Master 3S", "Chuot cong thai hoc cho dan van phong", 2490000, accessory.id);
        insertProductIfMissing(productDao, "Anker 65W Charger", "Sac nhanh da cong, nho gon", 990000, accessory.id);
        insertProductIfMissing(productDao, "Samsung T7 SSD 1TB", "SSD di dong toc do cao", 2890000, accessory.id);
    }

    private static void insertProductIfMissing(ProductDao productDao,
                                               String name,
                                               String description,
                                               double price,
                                               long categoryId) {
        Product existing = productDao.findByName(name);
        if (existing != null) {
            return;
        }

        Product product = new Product();
        product.name = name;
        product.description = description;
        product.price = price;
        product.categoryId = categoryId;
        productDao.insert(product);
    }
}
