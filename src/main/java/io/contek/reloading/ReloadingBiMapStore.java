package io.contek.reloading;

import com.google.common.collect.ImmutableBiMap;
import java.nio.file.Path;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ReloadingBiMapStore<Config, Key, Value>
    extends ReloadingStore<Config, ImmutableBiMap<Key, Value>> {

  protected ReloadingBiMapStore(Path configPath, Class<Config> configType) {
    super(configPath, configType);
  }

  @Nullable
  public final Value get(Key key) {
    ImmutableBiMap<Key, Value> map = getMap();
    return map.get(key);
  }

  public final ImmutableBiMap<Key, Value> getMap() {
    ImmutableBiMap<Key, Value> item = getItem();
    return item == null ? ImmutableBiMap.of() : item;
  }
}
