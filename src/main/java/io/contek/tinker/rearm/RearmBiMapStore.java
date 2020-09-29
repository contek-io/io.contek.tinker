package io.contek.tinker.rearm;

import com.google.common.collect.ImmutableBiMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

@ThreadSafe
public abstract class RearmBiMapStore<Config, Key, Value>
    extends RearmStore<Config, ImmutableBiMap<Key, Value>> {

  protected RearmBiMapStore(Path configPath, Class<Config> configType) {
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
    ImmutableBiMap<Key, Value> map = getMap();
    return map.get(key);
  }

  public final ImmutableBiMap<Key, Value> getMap() {
    ImmutableBiMap<Key, Value> item = getItem();
    return item == null ? ImmutableBiMap.of() : item;
  }

  /**
   * Listener which gets called when {@link RearmBiMapStore} has update.
   */
  @ThreadSafe
  public interface IListener<Key, Value> extends RearmStore.IListener<ImmutableBiMap<Key, Value>> {
  }
}
