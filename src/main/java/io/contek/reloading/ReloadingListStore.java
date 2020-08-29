package io.contek.reloading;

import com.google.common.collect.ImmutableList;
import java.nio.file.Path;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ReloadingListStore<Config, Value>
    extends ReloadingStore<Config, ImmutableList<Value>> {

  protected ReloadingListStore(Path configPath, Class<Config> configType) {
    super(configPath, configType);
  }

  @Nullable
  public final Value get(int index) {
    ImmutableList<Value> list = getList();
    return list.size() > index ? list.get(index) : null;
  }

  public final ImmutableList<Value> getList() {
    ImmutableList<Value> item = getItem();
    return item == null ? ImmutableList.of() : item;
  }
}
