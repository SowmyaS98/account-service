package com.banking.account.kafka;

import com.banking.account.event.AccountEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.account-events}")
    private String accountEventsTopic;

    public void publishAccountEvent(AccountEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);

            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(accountEventsTopic, event.getAccountId(), eventJson);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Account event published successfully: eventId={}, accountId={}, eventType={}, partition={}, offset={}",
                            event.getEventId(),
                            event.getAccountId(),
                            event.getEventType(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish account event: eventId={}, accountId={}, eventType={}",
                            event.getEventId(),
                            event.getAccountId(),
                            event.getEventType(),
                            ex);
                    throw new RuntimeException("Failed to publish account event", ex);
                }
            });

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize account event: {}", event, e);
            throw new RuntimeException("Failed to serialize account event", e);
        }
    }

    public CompletableFuture<SendResult<String, String>> publishAccountEventSync(AccountEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            return kafkaTemplate.send(accountEventsTopic, event.getAccountId(), eventJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize account event: {}", event, e);
            throw new RuntimeException("Failed to serialize account event", e);
        }
    }
}