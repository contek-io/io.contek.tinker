package io.contek.reloading;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import java.nio.file.Path;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ReloadingMultimapStore<Config, Key, Value>
    extends ReloadingStore<Config, ImmutableMultimap<Key, Value>> {

  protected ReloadingMultimapStore(Path configPath, Class<Config> configType) {
    super(configPath, configType);
  }

  public final ImmutableCollection<Value> get(Key key) {
    ImmutableMultimap<Key, Value> multimap = getMultimap();
    return multimap.get(key);
  }

  public final ImmutableMultimap<Key, Value> getMultimap() {
    ImmutableMultimap<Key, Value> item = getItem();
    return item == null ? ImmutableMultimap.of() : item;
  }
}
