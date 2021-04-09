package io.contek.tinker.reloading;

import com.google.common.collect.ImmutableBiMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

@ThreadSafe
public abstract class ReloadingBiMapStore<Key, Value> extends ReloadingStore<ImmutableBiMap<Key, Value>> {

  protected ReloadingBiMapStore(Path configPath, IParser<Key, Value> parser) {
    super(configPath, parser);
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
    ImmutableBiMap<Key, Value> item = getParsedConfig();
    return item == null ? ImmutableBiMap.of() : item;
  }

  /** Parser to read and parse {@link ImmutableBiMap} from a file. */
  @ThreadSafe
  public interface IParser<Key, Value> extends ReloadingStore.IParser<ImmutableBiMap<Key, Value>> {}

  /** Listener which gets called when {@link ReloadingBiMapStore} has update. */
  @ThreadSafe
  public interface IListener<Key, Value> extends ReloadingStore.IListener<ImmutableBiMap<Key, Value>> {}
}
