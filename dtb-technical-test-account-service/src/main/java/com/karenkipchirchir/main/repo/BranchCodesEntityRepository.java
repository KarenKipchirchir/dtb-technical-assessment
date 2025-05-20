package com.karenkipchirchir.main.repo;

import com.karenkipchirchir.main.models.database.BranchCodesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchCodesEntityRepository extends JpaRepository<BranchCodesEntity, Integer> {
    Optional<BranchCodesEntity> findByBranchCode(Integer branchCode);
}