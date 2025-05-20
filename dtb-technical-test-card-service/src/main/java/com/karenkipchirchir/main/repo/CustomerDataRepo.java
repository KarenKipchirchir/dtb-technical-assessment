package com.karenkipchirchir.main.repo;

import com.karenkipchirchir.main.models.database.CustomerBioDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerDataRepo extends JpaRepository<CustomerBioDataEntity,Integer> {

    Optional<CustomerBioDataEntity> findByCustomerId(Integer customerId);
}
