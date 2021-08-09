package ru.vlapin.consumer.dao;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlapin.consumer.model.Dog;

public interface DogDao extends JpaRepository<Dog, UUID> {
}
