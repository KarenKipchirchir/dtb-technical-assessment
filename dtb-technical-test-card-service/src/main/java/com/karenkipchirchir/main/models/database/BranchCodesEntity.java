package com.karenkipchirchir.main.models.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Entity(name = "DTB_CARDS_BRANCHES")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BranchCodesEntity {

    @Id
    @Column(name = "BRANCH_CODE")
    private Integer branchCode;
    private String branchName;

}
