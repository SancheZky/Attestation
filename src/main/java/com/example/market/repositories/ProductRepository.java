package com.example.market.repositories;


import com.example.market.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository  extends JpaRepository<Product, Integer> {

    // поиск по названию товара не зависимо от регистра
    List<Product> findByTitleContainingIgnoreCase(String title);

    // поиск по имени, фильтр по цене
    @Query(value = "SELECT * FROM product WHERE lower(title) LIKE %?1% AND price >=?2 AND price <= ?3", nativeQuery = true)
    List<Product> findByTitleAndFilterByPrice(String title, float ot, float Do);

    // поиск по имени, фильтр по цене, сортировка по возрастанию
    @Query(value = "SELECT * FROM product WHERE lower(title) LIKE %?1% AND price >=?2 and price <= ?3 ORDER BY price", nativeQuery = true)
    List<Product> finByTitleAndFilterByPriceOrderByPriceAsc(String title, float ot, float Do);

    // поиск по имени, фильтр по цене, сортировка по убыванию
    @Query(value = "SELECT * FROM product WHERE lower(title) LIKE %?1% AND price >=?2 and price <= ?3 ORDER BY price DESC ", nativeQuery = true)
    List<Product> finByTitleAndFilterByPriceOrderByPriceDesc(String title, float ot, float Do);

    @Query(value = "SELECT * FROM product WHERE lower(title) LIKE %?1% AND price >=?2 and price <= ?3 AND category_id = ?4 ORDER BY price", nativeQuery = true)
    List<Product> finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceAsc(String title, float ot, float Do, int category);

    @Query(value = "SELECT * FROM product WHERE lower(title) LIKE %?1% AND price >=?2 and price <= ?3 AND category_id = ?4 ORDER BY price DESC ", nativeQuery = true)
    List<Product> finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceDesc(String title, float ot, float Do, int category);

}
