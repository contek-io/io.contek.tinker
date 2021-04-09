package io.contek.tinker.reloading;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

@ThreadSafe
public abstract class ReloadingMapStore<Key, Value> extends ReloadingStore<ImmutableMap<Key, Value>> {

  protected ReloadingMapStore(Path configPath, IParser<Key, Value> parser) {
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
    Map<Key, Value> map = getMap();
    return map.get(key);
  }

  public final ImmutableMap<Key, Value> getMap() {
    ImmutableMap<Key, Value> item = getParsedConfig();
    return item == null ? ImmutableMap.of() : item;
  }

  /** Parser to read and parse {@link ImmutableMap} from a file. */
  @ThreadSafe
  public interface IParser<Key, Value> extends ReloadingStore.IParser<ImmutableMap<Key, Value>> {}

  /** Listener which gets called when {@link ReloadingMapStore} has update. */
  @ThreadSafe
  public interface IListener<Key, Value> extends ReloadingStore.IListener<ImmutableMap<Key, Value>> {}
}
