package com.example.market.repositories;

import com.example.market.Models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByPersonId(int id);

    @Query(value = "SELECT * FROM orders WHERE number LIKE %?1", nativeQuery = true)
    List<Order> findByNumberTail(String search);
}
