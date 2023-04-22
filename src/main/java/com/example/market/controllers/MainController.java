package com.example.market.controllers;

import com.example.market.repositories.ProductRepository;
import com.example.market.security.PersonDetails;
import com.example.market.Models.Person;
import com.example.market.services.PersonService;
import com.example.market.services.ProductService;
import com.example.market.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    private final PersonValidator personValidator;
    private final PersonService personService;

    private final ProductService productService;

    private final ProductRepository productRepository;

    public MainController(PersonValidator personValidator, PersonService personService, ProductService productService, ProductRepository productRepository) {
        this.personValidator = personValidator;
        this.personService = personService;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping("/account")
    public String index(Model model){
        // Получить объекс аутентификации
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        String role = personDetails.getPerson().getRole();
        if(role.equals("ROLE_ADMIN")){
            return "redirect:/admin";
        }
        model.addAttribute("products", productService.getAllProducts());
        return "/account/index";
    }


    // альтернативная запись
    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person){
        return "registration_form";
    }

    @PostMapping("/registration")
    public String registrateOrNot(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult){
        personValidator.validate(person, bindingResult);
        if(bindingResult.hasErrors()){
            return "/registration_form";
        }
        personService.registry(person);
        return "redirect:/account";
    }


    @GetMapping("/account/product/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("product", productService.getProductById(id));

        return "/account/info-product";
    }

    @PostMapping("/account/search")
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
                        model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                    } else if(contract.equals("appliances")) {
                        model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                    } else if (contract.equals("clothes")) {
                        model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                    }
                } else {
                    model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                }
            } else if(price.equals("sort_by_desc_price")){
                if(!contract.isEmpty()){
                    if(contract.equals("furniture")){
                        model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                    } else if(contract.equals("appliances")) {
                        model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                    } else if (contract.equals("clothes")) {
                        model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                    }
                } else {
                    model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                }
            } else {
                model.addAttribute("search_product", productRepository.findByTitleAndFilterByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
            }
        } else {
            model.addAttribute("search_product", productRepository.findByTitleContainingIgnoreCase(search.toLowerCase()));
        }


        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("value_search", search);
        model.addAttribute("value_price_ot", ot);
        model.addAttribute("value_price_do", Do);

        return "/account/index";

    }


}
