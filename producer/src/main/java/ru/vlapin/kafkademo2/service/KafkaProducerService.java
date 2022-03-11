package ru.vlapin.kafkademo2.service;

import io.vavr.Function1;
import java.util.Random;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import ru.vlapin.kafkademo2.common.Functions;

public sealed interface KafkaProducerService
    permits KafkaProducerServiceImpl {

  void produceMessages();
}

@Slf4j
@RequiredArgsConstructor
@ExtensionMethod(Functions.class)
@ConfigurationProperties("kafka-demo")
final class KafkaProducerServiceImpl implements KafkaProducerService {

  KafkaTemplate<String, String> template;

  // @formatter:off
  @Setter @NonFinal String topicName = "db-index";
  @Setter @NonFinal String key = "key1";
  // @formatter:on

  @Getter(lazy = true)
  Supplier<String> indexGenerator = Function1.<Integer, Integer>of(new Random()::nextInt)
      .andThen("%d"::formatted)
      .supply(10);

  @Override
  @Scheduled(
      fixedDelay = 2 * 1_000,
      initialDelay = 2 * 1_000)
  public void produceMessages() {
    template.send(topicName, getIndexGenerator().get())
        .addCallback(
            result -> log.info("Keyless message was send successfully!!! This is the result: {}", result),
            ex -> log.error("Keyless message was send with error - Kafka error: ", ex));

    template.send(topicName, key, getIndexGenerator().get())
        .addCallback(
            result -> log.info("Message with key was send successfully!!! This is the result: {}", result),
            ex -> log.error("Message with key was send with error - Kafka error: ", ex));
  }
}
