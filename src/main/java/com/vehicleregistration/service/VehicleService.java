package com.vehicleregistration.service;

import com.vehicleregistration.dao.VehicleDao;
import com.vehicleregistration.model.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleDao vehicleDao;

    @Autowired
    public VehicleService(VehicleDao vehicleDao) {
        this.vehicleDao = vehicleDao;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleDao.findAll();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleDao.findById(id);
    }

    public Optional<Vehicle> getVehicleByRegistrationNumber(String registrationNumber) {
        return vehicleDao.findByRegistrationNumber(registrationNumber);
    }

    public List<Vehicle> getVehiclesByCustomerId(Long customerId) {
        return vehicleDao.findByOwnerId(customerId);
    }

    public List<Vehicle> searchVehicles(String keyword) {
        return vehicleDao.searchVehicles(keyword);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleDao.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        vehicleDao.deleteById(id);
    }

    public boolean isRegistrationNumberUnique(String registrationNumber) {
        return !vehicleDao.existsByRegistrationNumber(registrationNumber);
    }
}


