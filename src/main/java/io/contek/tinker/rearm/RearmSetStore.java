package io.contek.tinker.rearm;

import com.google.common.collect.ImmutableSet;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;

@ThreadSafe
public abstract class RearmSetStore<Config, Value>
    extends RearmStore<Config, ImmutableSet<Value>> {

  protected RearmSetStore(Path configPath, Class<Config> configType) {
    super(configPath, configType);
  }

  public final ImmutableSet<Value> getSet() {
    ImmutableSet<Value> item = getItem();
    return item == null ? ImmutableSet.of() : item;
  }
}
