package com.karenkipchirchir.main.models.database;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Table
@Entity(name = "DTB_CARDS_ACCOUNTS")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccountDataEntity {

    @Id
    @Column(name = "ACCOUNT_ID")
    private Integer accountId;
    private String iban;
    private String bicSwift;
    private String currency;

    private String status;
    private String requestHash;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private CustomerBioDataEntity customer;


    @ManyToOne
    @JoinColumn(name = "BRANCH")
    private BranchCodesEntity branch;

    @OneToMany(mappedBy = "accountId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CardsEntity> cardsEntities;


}
