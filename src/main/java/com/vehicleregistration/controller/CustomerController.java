package com.vehicleregistration.controller;

import com.vehicleregistration.model.Customer;
import com.vehicleregistration.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@Api(tags = "Customer Controller", description = "Operations related to customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @ApiOperation("Get all customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @ApiOperation("Get customer by ID")
    public ResponseEntity<Customer> getCustomerById(
            @ApiParam(value = "Customer ID", required = true) @PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @ApiOperation("Get customer by email")
    public ResponseEntity<Customer> getCustomerByEmail(
            @ApiParam(value = "Customer Email", required = true) @PathVariable String email) {
        return customerService.getCustomerByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @ApiOperation("Search customers by keyword")
    public ResponseEntity<List<Customer>> searchCustomers(
            @ApiParam(value = "Search keyword", required = true) @RequestParam String keyword) {
        return ResponseEntity.ok(customerService.searchCustomers(keyword));
    }

    @PostMapping
    @ApiOperation("Register a new customer")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody Customer customer) {
        if (!customerService.isEmailUnique(customer.getEmail())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
        Customer savedCustomer = customerService.saveCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    @PutMapping("/{id}")
    @ApiOperation("Update a customer")
    public ResponseEntity<?> updateCustomer(
            @ApiParam(value = "Customer ID", required = true) @PathVariable Long id,
            @Valid @RequestBody Customer customer) {
        return customerService.getCustomerById(id)
                .map(existingCustomer -> {
                    // Check if email is changed and is unique
                    if (!existingCustomer.getEmail().equals(customer.getEmail()) &&
                            !customerService.isEmailUnique(customer.getEmail())) {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Email already exists");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
                    }

                    customer.setId(id);
                    customer.setVehicles(existingCustomer.getVehicles());
                    Customer updatedCustomer = customerService.saveCustomer(customer);
                    return ResponseEntity.ok(updatedCustomer);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete a customer")
    public ResponseEntity<Void> deleteCustomer(
            @ApiParam(value = "Customer ID", required = true) @PathVariable Long id) {
        if (!customerService.getCustomerById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}