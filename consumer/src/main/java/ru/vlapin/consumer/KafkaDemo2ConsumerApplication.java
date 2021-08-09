package ru.vlapin.consumer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
@ConfigurationPropertiesScan
public class KafkaDemo2ConsumerApplication {

  public static void main(String[] args) {
    SpringApplication.run(KafkaDemo2ConsumerApplication.class, args);
  }

  @Bean
  NewTopic topic(@Value("${kafka-demo.response-topic-name:db-response}") String topicName) {
    return TopicBuilder.name(topicName)
        .partitions(10)
        .replicas(1).build();
  }
}
