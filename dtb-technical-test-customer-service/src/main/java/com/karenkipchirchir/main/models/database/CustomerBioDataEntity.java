package com.karenkipchirchir.main.models.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table
@Entity(name = "DTB_CARDS_CUSTOMERS")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerBioDataEntity {

    @Id
    @Column(name = "CUSTOMER_ID")
    private Integer customerId;
    private String messageId;
    private String firstName;
    private String lastName;
    private String otherName;
    private String status;
    private String requestHash;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
