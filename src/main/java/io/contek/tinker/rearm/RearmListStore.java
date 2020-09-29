package io.contek.tinker.rearm;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

@ThreadSafe
public abstract class RearmListStore<Config, Value>
    extends RearmStore<Config, ImmutableList<Value>> {

  protected RearmListStore(Path configPath, Class<Config> configType) {
    super(configPath, configType);
  }

  public final Value get(int index) throws NoSuchElementException {
    return getOrThrow(index, NoSuchElementException::new);
  }

  public final <E extends Throwable> Value getOrThrow(int index, Supplier<E> t) throws E {
    Value value = getNullable(index);
    if (value == null) {
      throw t.get();
    }
    return value;
  }


  @Nullable
  public final Value getNullable(int index) {
    List<Value> list = getList();
    return list.size() > index ? list.get(index) : null;
  }

  public final ImmutableList<Value> getList() {
    ImmutableList<Value> item = getItem();
    return item == null ? ImmutableList.of() : item;
  }
}
