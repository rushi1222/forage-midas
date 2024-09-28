package com.jpmc.midascore.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class KafkaTransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTransactionListener.class);

    @Value("${general.kafka-topic}")
    private String kafkaTopic;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Listen to the Kafka topic and handle incoming transactions
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void listen(Transaction transaction) {
        // Log the incoming transaction
        logger.info("Received Transaction: " + transaction);

        // Retrieve sender and recipient from the database
        Optional<UserRecord> senderOptional = Optional.ofNullable(userRepository.findById(transaction.getSenderId()));
        Optional<UserRecord> recipientOptional = Optional.ofNullable(userRepository.findById(transaction.getRecipientId()));

        // Validate the sender and recipient exist
        if (senderOptional.isEmpty() || recipientOptional.isEmpty()) {
            logger.warn("Transaction failed: Invalid sender or recipient ID.");
            return; // Exit if validation fails
        }

        UserRecord sender = senderOptional.get();
        UserRecord recipient = recipientOptional.get();

        // Validate the sender's balance
        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Transaction failed: Sender {} has insufficient balance.", sender.getName());
            return; // Exit if validation fails
        }

        // Perform the transaction and update balances
        float amount = transaction.getAmount();
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        // Save the updated balances in the database
        userRepository.save(sender);
        userRepository.save(recipient);


        // Record the transaction in the database
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount);
        transactionRepository.save(transactionRecord);

        logger.info("Transaction processed successfully and recorded in the database. Sender: {}, Recipient: {}, Amount: {}", sender.getName(), recipient.getName(), amount);
    }
}
