package com.karenkipchirchir.main.repo;

import com.karenkipchirchir.main.models.CardTypes;
import com.karenkipchirchir.main.models.database.CardsEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardsEntityRepository extends JpaRepository<CardsEntity, Integer> {
  List<CardsEntity> findByAccountId_AccountId(Integer accountId);

  @Query(value = "SELECT MAX(CARD_ID) FROM DTB_CARDS_RECORDS", nativeQuery = true)
  Integer findMaxCardNumber();

  List<CardsEntity> findByCardAliasLikeAndCardTypeAndPan(String cardAlias, CardTypes cardType, String pan, Pageable paging);

  @Query("""
          select d from DTB_CARDS_RECORDS d
          where d.cardAlias like ?1 or d.cardAlias like concat('%', ?2, '%') and d.cardType = ?3 and d.pan = ?4""")
  List<CardsEntity> findByCardAliasLikeOrCardAliasContainsAndCardTypeAndPan(String cardAlias, String cardAlias1, CardTypes cardType, String pan, Pageable pageable);

  Optional<CardsEntity> findByCardId(Integer cardId);

  Optional<CardsEntity> findByRequestHashAndCreatedAtAfter(String requestHash, LocalDateTime createdAt);
}