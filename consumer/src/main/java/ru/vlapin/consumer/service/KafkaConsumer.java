package ru.vlapin.consumer.service;

import static java.util.Arrays.stream;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import ru.vlapin.consumer.dao.DogDao;
import ru.vlapin.consumer.model.Dog;

@Slf4j
@RequiredArgsConstructor
@ConfigurationProperties("kafka-demo")
public class KafkaConsumer {

  DogDao dogDao;
  KafkaTemplate<String, String> kafkaTemplate;

  @Setter
  @NonFinal
  String responseTopicName = "db-response";

  @Getter(lazy = true)
  List<UUID> ids =
      stream("Жучка, Шарик, Барбос, Рэкс, Татошка, Айза, Кнопка, Бим, Жужа, Боня".split(", "))
          .map(Dog::new)
          .map(dogDao::save)
          .map(Dog::getId)
          .toList();

  @KafkaListener(
      id = "myId",
      topics = "${kafka-demo.topic-name:db-index}")
  void listen(String index) {
    log.info("message: {}", index);

    Optional.of(index)
        .filter(s -> s.chars().allMatch(Character::isDigit))
        .map(Integer::parseInt)
        .map(getIds()::get)
        .flatMap(dogDao::findById)
        .map(Dog::getName)
        .map(s -> kafkaTemplate.send(responseTopicName, s))
        .ifPresent(
            future -> future.addCallback(
                result -> log.info("Keyless message was send successfully!!! This is the result: {}", result),
                ex -> log.error("Keyless message was send with error - Kafka error: {}", ex.toString())));
  }
}
