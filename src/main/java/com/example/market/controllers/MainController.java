package com.example.market.controllers;

import com.example.market.Models.Person;
import com.example.market.security.PersonDetails;
import com.example.market.services.PersonService;
import com.example.market.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    private final PersonValidator personValidator;
    private final PersonService personService;

    public MainController(PersonValidator personValidator, PersonService personService) {
        this.personValidator = personValidator;
        this.personService = personService;
    }

    @GetMapping("/index")
    public String index(){
        // Получить объекс аутентификации
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        System.out.println(personDetails.getPerson().getLogin());


        return "index";
    }


//    @GetMapping("/registration")
//    public String registration(Model model){
//        model.addAttribute("person", new Person());
//        return "registration_form";
//    }

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
        return "redirect:/index";
    }

}
