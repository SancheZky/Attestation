package com.example.market.repositories;

import com.example.market.Models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByPersonId(int id);

    @Modifying
    @Query(value = "delete from product_cart where person_id = ?1 AND product_id = ?2", nativeQuery = true)
    void deleteCartByPersonIdAndProductId(int person_id, int product_id);


}
