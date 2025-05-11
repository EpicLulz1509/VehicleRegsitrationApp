package com.vehicleregistration.dao;

import com.vehicleregistration.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleDao extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    List<Vehicle> findByMakeIgnoreCase(String make);

    List<Vehicle> findByModelIgnoreCase(String model);

    List<Vehicle> findByYearBetween(Integer startYear, Integer endYear);

    List<Vehicle> findByOwnerId(Long ownerId);

    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.make) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.vehicleregistration.model) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.registrationNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Vehicle> searchVehicles(String keyword);

    boolean existsByRegistrationNumber(String registrationNumber);
}
