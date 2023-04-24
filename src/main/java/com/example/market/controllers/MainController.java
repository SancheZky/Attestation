package com.example.market.controllers;

import com.example.market.Models.Cart;
import com.example.market.Models.Order;
import com.example.market.Models.Product;
import com.example.market.enums.Status;
import com.example.market.repositories.CartRepository;
import com.example.market.repositories.OrderRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class MainController {

    private final PersonValidator personValidator;
    private final PersonService personService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;


    public MainController(PersonValidator personValidator, PersonService personService, ProductService productService, ProductRepository productRepository, CartRepository cartRepository, OrderRepository orderRepository) {
        this.personValidator = personValidator;
        this.personService = personService;
        this.productService = productService;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/account")
    public String index(Model model) {
        // Получить объекс аутентификации
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        String role = personDetails.getPerson().getRole();
        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin";
        }
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("user", getSessionPerson().getLogin());
        return "/account/index";
    }


    // альтернативная запись
    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person) {
        return "registration_form";
    }

    @PostMapping("/registration")
    public String registrateOrNot(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/registration_form";
        }
        personService.registry(person);
        return "redirect:/account";
    }


    @GetMapping("/account/product/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model) {
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

        if (!ot.isEmpty() & !Do.isEmpty()) {
            if (price.equals("sort_by_asc_price")) {
                if (!contract.isEmpty()) {
                    if (contract.equals("furniture")) {
                        model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                    } else if (contract.equals("appliances")) {
                        model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                    } else if (contract.equals("clothes")) {
                        model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                    }
                } else {
                    model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                }
            } else if (price.equals("sort_by_desc_price")) {
                if (!contract.isEmpty()) {
                    if (contract.equals("furniture")) {
                        model.addAttribute("search_product", productRepository.finByTitleAndFilterByPriceAndFilterByCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                    } else if (contract.equals("appliances")) {
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


    @GetMapping("/account/cart/add/{id}")
    public String addProductInCart(@PathVariable("id") int id, Model model) {
        Product product = productService.getProductById(id);
        int person_id = getSessionPersonId();
        Cart cart = new Cart(person_id, product.getId());
        cartRepository.save(cart);

        return "redirect:/account/cart";
    }

    @GetMapping("/account/cart")
    public String cart(Model model) {
        int person_id = getSessionPersonId();

        List<Cart> cartList = cartRepository.findByPersonId(person_id);
        List<Product> products = new ArrayList<>();

        float totalPrice = 0;

        for (Cart cart : cartList) {
            Product product = productService.getProductById(cart.getProductId());
            products.add(product);
            totalPrice += product.getPrice();
        }
        model.addAttribute("products", products);
        model.addAttribute("totalPrice", totalPrice);

        return "/account/cart";
    }

    @GetMapping("/account/cart/delete/{id}")
    public String deleteProductFromCart(Model model, @PathVariable("id") int id) {
        int person_id = getSessionPersonId();
        cartRepository.deleteCartByPersonIdAndProductId(person_id, id);
        return "redirect:/account/cart";
    }

    public int getSessionPersonId() {
        return getSessionPerson().getId();
    }

    public Person getSessionPerson(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.getPerson();
    }


    @GetMapping("/account/order/create")
    public String order(){
        int person_id = getSessionPersonId();

        List<Cart> cartList = cartRepository.findByPersonId(person_id);
        List<Product> products = new ArrayList<>();

        float totalPrice = 0;

        for (Cart cart : cartList) {
            Product product = productService.getProductById(cart.getProductId());
            products.add(product);
            totalPrice += product.getPrice();
        }

        String uuid = UUID.randomUUID().toString();
        for(Product product : products){
            Order newOrder = new Order(uuid,product, getSessionPerson(), 1, product.getPrice(), Status.Оформлен);
            orderRepository.save(newOrder);
            cartRepository.deleteCartByPersonIdAndProductId(person_id, product.getId());
        }
        return "redirect:/account/orders";
    }


    @GetMapping("/account/orders")
    public String orderList(Model model){
        int id = getSessionPersonId();
        List<Order> orderList = orderRepository.findByPersonId(id);
        model.addAttribute("orders", orderList);

        return "/account/orders";
    }

}
