package com.example.customer_service.business.service.impl;

import com.example.customer_service.business.mappers.CustomerMapper;
import com.example.customer_service.business.repository.CustomerRepository;
import com.example.customer_service.business.repository.model.CustomerDAO;
import com.example.customer_service.business.service.CustomerService;
import com.example.customer_service.model.Customer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    CustomerRepository repository;
    @Autowired
    CustomerMapper mapper;

    private final WebClient webClient;

    public CustomerServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8765/accounts").build();
    }

    public CustomerServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<Customer> getAllCustomers() {
        log.info("Looking for all customers, returning list");
        List<Customer> listOfDao = repository.findAll()
                .stream()
                .map(mapper::mapFromDao)
                .collect(Collectors.toList());
        log.info("Returning list with size: {}", listOfDao.size());
        return listOfDao;
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        log.info("Looking for customer with id {}", id);
        return repository
                .findById(id)
                .flatMap(customer -> Optional.ofNullable(mapper.mapFromDao(customer)));
    }

    @Override
    public Optional<Customer> editCustomer(Long id, Customer updatedCustomer) {
        log.info("Updating Customer entry");
        if(repository.existsById(id)){
            log.info("Customer entry with id {} is updated", id);
            
            return Optional.ofNullable(mapper.mapFromDao(repository.save(mapper.mapToDao(updatedCustomer))));
        }
        log.warn("Customer entry with id {} is not updated", id);
        return Optional.empty();
    }


    @Override
    public Customer saveCustomer(Customer customer) {
        List<CustomerDAO> existingCustomers = repository.findAll();
        for (CustomerDAO existingCustomer : existingCustomers) {
            if (existingCustomer.getName().equals(customer.getName())
                    && existingCustomer.getSurname().equals(customer.getSurname())
                    && existingCustomer.getIdentityNumber().equals(customer.getIdentityNumber())) {
                log.warn("Customer with the same name, surname, and identity number already exists");
                throw new IllegalArgumentException("Customer with the same name, surname, and identity number already exists");
            }
        } log.info("Saving new Customer entry");
        return mapper.mapFromDao(repository.save(mapper.mapToDao(customer)));
    }
    
    @Override
    public void deleteCustomerById(Long id) {
        log.info("Deleting customer with id {}", id);
        repository.deleteById(id);
    }

    public Flux<JsonNode> getAccountsByCustomerId(String customerId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getByCustomerId").queryParam("customerId", customerId).build())
                .retrieve()
                .bodyToFlux(JsonNode.class);
    }
}
