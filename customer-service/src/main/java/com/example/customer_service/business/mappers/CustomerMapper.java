package com.example.customer_service.business.mappers;

import com.example.customer_service.business.repository.model.CustomerDAO;
import com.example.customer_service.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer mapFromDao(CustomerDAO customerDAO);
    CustomerDAO mapToDao(Customer customer);
}
