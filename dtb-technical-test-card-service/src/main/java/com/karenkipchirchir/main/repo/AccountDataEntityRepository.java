package com.karenkipchirchir.main.repo;

import com.karenkipchirchir.main.models.database.AccountDataEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountDataEntityRepository extends JpaRepository<AccountDataEntity, Integer> {
  Optional<AccountDataEntity> findByRequestHashAndCreatedAtAfter(String requestHash, LocalDateTime createdAt);

  List<AccountDataEntity> findByIbanAndBicSwiftAndCardsEntities_CardAliasLikeOrCardsEntities_CardAliasContains(String iban, String bicSwift, String cardAlias, String cardAlias1, Pageable paging);

  Optional<AccountDataEntity> findByAccountId(Integer accountId);

  @Query(value = "SELECT MAX(ACCOUNT_ID) FROM DTB_CARDS_ACCOUNTS", nativeQuery = true)
  Integer findMaxAccountNumber();

  List<AccountDataEntity> findByCustomer_CustomerId(Integer customerId);
}