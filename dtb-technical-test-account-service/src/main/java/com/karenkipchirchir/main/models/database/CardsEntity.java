package com.karenkipchirchir.main.models.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.karenkipchirchir.main.models.CardTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table
@Entity(name = "DTB_CARDS_RECORDS")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CardsEntity {
    @Id
    @Column(name = "CARD_ID")
    private Integer cardId;

    private String cardAlias;
    private CardTypes cardType;

    private String pan;
    private String cvv;

    private String status;
    private String requestHash;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private AccountDataEntity accountId;


}
