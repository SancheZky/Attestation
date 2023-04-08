package com.example.market.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Normalized;

@Entity
public class Product {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", columnDefinition = "text", nullable = false,  unique = true)
    @NotEmpty(message = "Название товара не может быть пустым ")
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    @NotEmpty(message = "Поле описание не должно быть пустым")
    private String description;

    @Column(name = "price", nullable = false)
    @Min(value = 1, message = "Цена не может быть отрицательной или ноль")
    private float price;



    @Column(name = "warehouse", nullable = false, columnDefinition = "text")
    @NotEmpty(message = "Склад является обязатльным полем ")
    private String warehouse;

    @Column(name = "seller")
    @NotEmpty(message = "Продавец является обязатлеьным полем")
    private String seller;






}
