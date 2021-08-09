package ru.vlapin.kafkademo2.common;

import io.vavr.Function0;
import io.vavr.Function1;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Functions {

  public <T, R> Function0<R> supply(Function1<T, R> self, T t) {
    return () -> self.apply(t);
  }
}
