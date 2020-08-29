package io.contek.reloading;

import com.google.common.collect.ImmutableSet;
import java.nio.file.Path;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ReloadingSetStore<Config, Value>
    extends ReloadingStore<Config, ImmutableSet<Value>> {

  protected ReloadingSetStore(Path configPath, Class<Config> configType) {
    super(configPath, configType);
  }

  public final ImmutableSet<Value> getSet() {
    ImmutableSet<Value> item = getItem();
    return item == null ? ImmutableSet.of() : item;
  }
}
