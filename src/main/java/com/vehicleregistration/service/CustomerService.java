package com.vehicleregistration.service;

import com.vehicleregistration.dao.CustomerDao;
import com.vehicleregistration.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    @Autowired
    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerDao.findById(id);
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerDao.findByEmail(email);
    }

    public List<Customer> searchCustomers(String keyword) {
        return customerDao.searchCustomers(keyword);
    }

    public Customer saveCustomer(Customer customer) {
        return customerDao.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerDao.deleteById(id);
    }

    public boolean isEmailUnique(String email) {
        return !customerDao.existsByEmail(email);
    }
}


