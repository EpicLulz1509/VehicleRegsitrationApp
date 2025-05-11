package com.vehicleregistration.controller;

import com.vehicleregistration.model.Vehicle;
import com.vehicleregistration.service.CustomerService;
import com.vehicleregistration.service.VehicleService;
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
@RequestMapping("/api/vehicles")
@Api(tags = "Vehicle Controller", description = "Operations related to vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final CustomerService customerService;

    @Autowired
    public VehicleController(VehicleService vehicleService, CustomerService customerService) {
        this.vehicleService = vehicleService;
        this.customerService = customerService;
    }

    @GetMapping
    @ApiOperation("Get all vehicles")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    @ApiOperation("Get vehicle by ID")
    public ResponseEntity<Vehicle> getVehicleById(
            @ApiParam(value = "Vehicle ID", required = true) @PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/registration/{registrationNumber}")
    @ApiOperation("Get vehicle by registration number")
    public ResponseEntity<Vehicle> getVehicleByRegistrationNumber(
            @ApiParam(value = "Registration Number", required = true) @PathVariable String registrationNumber) {
        return vehicleService.getVehicleByRegistrationNumber(registrationNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @ApiOperation("Get vehicles by customer ID")
    public ResponseEntity<List<Vehicle>> getVehiclesByCustomerId(
            @ApiParam(value = "Customer ID", required = true) @PathVariable Long id) {
        if (!customerService.getCustomerById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehicleService.getVehiclesByCustomerId(id));
    }

    @GetMapping("/search")
    @ApiOperation("Search vehicles by keyword")
    public ResponseEntity<List<Vehicle>> searchVehicles(
            @ApiParam(value = "Search keyword", required = true) @RequestParam String keyword) {
        return ResponseEntity.ok(vehicleService.searchVehicles(keyword));
    }

    @PostMapping
    @ApiOperation("Register a new vehicle")
    public ResponseEntity<?> registerVehicle(@Valid @RequestBody Vehicle vehicle) {
        if (!vehicleService.isRegistrationNumberUnique(vehicle.getRegistrationNumber())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Registration number already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        return customerService.getCustomerById(vehicle.getOwner().getId())
                .map(customer -> {
                    vehicle.setOwner(customer);
                    Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
                })
                .orElseGet(() -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Customer not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                });
    }

    @PutMapping("/{id}")
    @ApiOperation("Update a vehicle")
    public ResponseEntity<?> updateVehicle(
            @ApiParam(value = "Vehicle ID", required = true) @PathVariable Long id,
            @Valid @RequestBody Vehicle vehicle) {
        return vehicleService.getVehicleById(id)
                .map(existingVehicle -> {
                    // Check if registration number is changed and is unique
                    if (!existingVehicle.getRegistrationNumber().equals(vehicle.getRegistrationNumber()) &&
                            !vehicleService.isRegistrationNumberUnique(vehicle.getRegistrationNumber())) {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Registration number already exists");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
                    }

                    return customerService.getCustomerById(vehicle.getOwner().getId())
                            .map(customer -> {
                                vehicle.setId(id);
                                vehicle.setOwner(customer);
                                Vehicle updatedVehicle = vehicleService.saveVehicle(vehicle);
                                return ResponseEntity.ok(updatedVehicle);
                            })
                            .orElseGet(() -> {
                                Map<String, String> errorResponse = new HashMap<>();
                                errorResponse.put("error", "Customer not found");
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                            });
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete a vehicle")
    public ResponseEntity<Void> deleteVehicle(
            @ApiParam(value = "Vehicle ID", required = true) @PathVariable Long id) {
        if (!vehicleService.getVehicleById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}