package io.contek.tinker.rearm;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

@ThreadSafe
public abstract class RearmMapStore<Config, Key, Value>
    extends RearmStore<Config, ImmutableMap<Key, Value>> {

  protected RearmMapStore(Path configPath, Class<Config> configType) {
    super(configPath, configType);
  }

  public final Value get(Key key) throws NoSuchElementException {
    return getOrThrow(key, NoSuchElementException::new);
  }

  public final <E extends Throwable> Value getOrThrow(Key key, Supplier<E> t) throws E {
    Value value = getNullable(key);
    if (value == null) {
      throw t.get();
    }
    return value;
  }

  @Nullable
  public final Value getNullable(Key key) {
    Map<Key, Value> map = getMap();
    return map.get(key);
  }

  public final ImmutableMap<Key, Value> getMap() {
    ImmutableMap<Key, Value> item = getItem();
    return item == null ? ImmutableMap.of() : item;
  }

  /**
   * Listener which gets called when {@link RearmMapStore} has update.
   */
  @ThreadSafe
  public interface IListener<Key, Value> extends RearmStore.IListener<ImmutableMap<Key, Value>> {
  }
}
