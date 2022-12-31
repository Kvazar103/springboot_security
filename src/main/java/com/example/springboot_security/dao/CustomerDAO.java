package com.example.springboot_security.dao;

import com.example.springboot_security.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerDAO extends JpaRepository<Customer,Integer> {

     Customer findByLogin(String login);
}
