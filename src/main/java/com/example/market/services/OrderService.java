package com.example.market.services;

import com.example.market.Models.Order;
import com.example.market.enums.Status;
import com.example.market.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public void save(Order order){
        orderRepository.save(order);
    }

    public void setStatus(int id, Status status){
        Optional<Order> orderOptional = orderRepository.findById(id);
        if(orderOptional.isPresent()){
            Order order = orderOptional.get();
            order.setStatus(status);
            orderRepository.save(order);
        }

    }

    public List<Order> getByNumberTail(String tail){
        return orderRepository.findByNumberTail(tail);
    }


}
