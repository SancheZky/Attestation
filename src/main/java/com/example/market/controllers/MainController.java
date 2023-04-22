package com.example.market.controllers;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    private final PersonValidator personValidator;
    private final PersonService personService;

    private final ProductService productService;

    public MainController(PersonValidator personValidator, PersonService personService, ProductService productService) {
        this.personValidator = personValidator;
        this.personService = personService;
        this.productService = productService;
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


}
