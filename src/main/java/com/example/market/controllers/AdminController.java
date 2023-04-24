package com.example.market.controllers;

import com.example.market.Models.Category;
import com.example.market.Models.Image;
import com.example.market.Models.Order;
import com.example.market.enums.Status;
import com.example.market.repositories.CategoryRepository;
import com.example.market.Models.Product;
import com.example.market.services.OrderService;
import com.example.market.services.PersonService;
import com.example.market.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class AdminController {

    @Value("${upload.path}")
    private String uploadPath;
    private final CategoryRepository categoryRepository;
    private final ProductService productService;

    private final PersonService personService;

    private final OrderService orderService;

    public AdminController(CategoryRepository categoryRepository, ProductService productService, PersonService personService, OrderService orderService) {
        this.categoryRepository = categoryRepository;
        this.productService = productService;
        this.personService = personService;
        this.orderService = orderService;
    }

    @GetMapping("/admin")
    public String admin(Model model){
        model.addAttribute("products", productService.getAllProducts());

        return "admin/admin";
    }

    @GetMapping("/admin/product/add")
    public String addProductForm(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/add-product-form";
    }


    @PostMapping("/admin/product/add")
    public String addProductForm(
            @ModelAttribute("product") @Valid Product product,
            BindingResult bindingResult,
            @RequestParam("file_1") MultipartFile file_1,
            @RequestParam("file_2") MultipartFile file_2,
            @RequestParam("file_3") MultipartFile file_3,
            @RequestParam("file_4") MultipartFile file_4,
            @RequestParam("file_5") MultipartFile file_5,
            @RequestParam("category") int categoryId,
            Model model
            ) throws IOException {
        Category category_db = (Category) categoryRepository.findById(categoryId).orElseThrow();
        System.out.println(category_db.getName());
        if(bindingResult.hasErrors()) {
            model.addAttribute("category", categoryRepository.findAll());
            return "product/add-product-form";
        }
        addFile(product,file_1);
        addFile(product,file_2);
        addFile(product,file_3);
        addFile(product,file_4);
        addFile(product,file_5);

        productService.saveProduct(product,category_db);

        return "redirect:/admin";

    }
    @GetMapping("/admin/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id){
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/product/edit/{id}")
    public String editProduct(Model model, @PathVariable("id") int id){
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("category", categoryRepository.findAll());
        return "product/edit-product-form";
    }

    @PostMapping("/admin/product/edit/{id}")
    public String editProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @PathVariable("id") int id, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("category", categoryRepository.findAll());
            return "product/edit-product-form";
        }
        productService.updateProduct(id, product);
        return "redirect:/admin";
    }

    @GetMapping("/admin/persons")
    private String users(Model model){
        model.addAttribute("persons", personService.getAllPersons());
        return "admin/persons_admin";
    }

    @GetMapping("/admin/person/setAdminRole/{id}")
    public String personSetAdminRole(@PathVariable("id") int id){
        personService.setRoleADMIN(id);
        return "redirect:/admin/persons";
    }

    @GetMapping("/admin/person/setUserRole/{id}")
    public String personSetUserRole(@PathVariable("id") int id){
        personService.setRoleUSER(id);
        return "redirect:/admin/persons";
    }

    @GetMapping("/admin/orders")
    public String orders(Model model){
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("tail", "");
        return "admin/orders_admin";
    }

    @PostMapping("/admin/order/status/{id}")
    public String orders(Model model, @PathVariable("id") int id, @RequestParam("status") Status status){
        orderService.setStatus(id, status);
        return "redirect:/admin/orders";
    }

    @GetMapping("/order/search")
    public String orderSearch(Model model, @RequestParam("tail") String tail){
        model.addAttribute("orders", orderService.getByNumberTail(tail));
        model.addAttribute("statuses", Status.values());
        model.addAttribute("tail", tail);

        return "admin/orders_admin";
    }


    private void addFile(Product product, MultipartFile file) throws IOException {
        if(file != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));

            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }
    }



}
