package com.example.springboot_security.controllers;

import com.example.springboot_security.dao.CustomerDAO;
import com.example.springboot_security.models.Customer;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MainController {

    private CustomerDAO customerDAO;
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String open(){
        return "open";
    }
    @GetMapping("/open2")
    public String open2(){
        return "should deny";
    }
    @PostMapping("/save")
    public void save(@RequestBody Customer customer){
        String password=customer.getPassword();
        String encode=passwordEncoder.encode(password);//закодовуємо пароль для збереження в бд(паролі в бд мають бути зашифровані)
        customer.setPassword(encode);
        customerDAO.save(customer);
    }
    @DeleteMapping("/deleteALL")
    public void delete(){
        customerDAO.deleteAll();
    }
    @GetMapping("/secure")
    public String secure(){  //цей метод контролер спрацьовує тільки якщо ми збережемо в бд відповід обєкт в якого буде відповідна роль
        return "secure";
    }
}
