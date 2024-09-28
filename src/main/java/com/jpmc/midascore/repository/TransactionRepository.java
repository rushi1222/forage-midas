package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.TransactionRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionRecord, Long> {
    TransactionRecord findById(long id);

    TransactionRecord findBySenderId(long senderId);

    TransactionRecord findByRecipientId(long recipientId);

    TransactionRecord findByAmount(float amount);

    TransactionRecord findBySenderIdAndRecipientIdAndAmount(long senderId, long recipientId, float amount);

    TransactionRecord findBySenderIdAndAmount(long senderId, float amount);

    TransactionRecord findByRecipientIdAndAmount(long recipientId, float amount);
}
