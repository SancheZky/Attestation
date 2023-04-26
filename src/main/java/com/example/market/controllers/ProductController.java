package com.example.market.controllers;

import com.example.market.repositories.ProductRepository;
import com.example.market.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;

    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String getAllProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/product/products";
    }

    @GetMapping("/product/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("login", false);
        return "/product/info-product";
    }

    @PostMapping("/product/search")
    public String productSearch(
            @RequestParam("search") String search,
            @RequestParam("ot") String ot,
            @RequestParam("do") String Do,
            @RequestParam(value = "price", required = false, defaultValue = "") String price,
            @RequestParam(value = "contract", required = false, defaultValue = "") String contract,
            Model model
            ) {

        if(!ot.isEmpty() & !Do.isEmpty()){
            if(price.equals("sort_by_asc_price")){
                if(!contract.isEmpty()){
                    if(contract.equals("furniture")){
                        model.addAttribute("products", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                    } else if(contract.equals("appliances")) {
                        model.addAttribute("products", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                    } else if (contract.equals("clothes")) {
                        model.addAttribute("products", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                    }
                } else {
                    model.addAttribute("products", productRepository.finByTitleAndFilterByPriceOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                }
            } else if(price.equals("sort_by_desc_price")){
                if(!contract.isEmpty()){
                    if(contract.equals("furniture")){
                        model.addAttribute("products", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                    } else if(contract.equals("appliances")) {
                        model.addAttribute("products", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                    } else if (contract.equals("clothes")) {
                        model.addAttribute("products", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                    }
                } else {
                    model.addAttribute("products", productRepository.finByTitleAndFilterByPriceOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                }
            } else {
                model.addAttribute("products", productRepository.findByTitleAndFilterByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
            }
        } else {
            model.addAttribute("products", productRepository.findByTitleContainingIgnoreCase(search.toLowerCase()));
        }


        model.addAttribute("value_search", search);
        model.addAttribute("value_price_ot", ot);
        model.addAttribute("value_price_do", Do);

        return "/product/products";

    }

}
