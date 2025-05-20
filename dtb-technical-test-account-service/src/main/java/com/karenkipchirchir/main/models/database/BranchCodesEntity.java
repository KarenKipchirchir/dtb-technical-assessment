package com.karenkipchirchir.main.models.database;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
