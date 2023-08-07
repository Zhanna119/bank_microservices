package com.example.customer_service.business.service;


import com.example.customer_service.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Optional<Customer> getCustomerById(Long id);
    Optional<Customer> editCustomer(Long id, Customer updatedCustomer);
    Customer saveCustomer(Customer customer);
    void deleteCustomerById(Long id);


}
