package io.contek.reloading;

import com.google.common.collect.ImmutableMap;
import java.nio.file.Path;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ReloadingMapStore<Config, Key, Value>
    extends ReloadingStore<Config, ImmutableMap<Key, Value>> {

  protected ReloadingMapStore(Path configPath, Class<Config> configType) {
    super(configPath, configType);
  }

  @Nullable
  public final Value get(Key key) {
    ImmutableMap<Key, Value> map = getMap();
    return map.get(key);
  }

  public final ImmutableMap<Key, Value> getMap() {
    ImmutableMap<Key, Value> item = getItem();
    return item == null ? ImmutableMap.of() : item;
  }
}
