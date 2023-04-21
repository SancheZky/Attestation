package com.example.market.Models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    private String name;


    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<Product> products;



}
