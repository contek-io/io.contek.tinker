package io.contek.reloading;

import com.google.common.collect.ImmutableTable;
import java.nio.file.Path;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ReloadingTableStore<Config, RowKey, ColumnKey, Value>
    extends ReloadingStore<Config, ImmutableTable<RowKey, ColumnKey, Value>> {

  protected ReloadingTableStore(Path configPath, Class<Config> configType) {
    super(configPath, configType);
  }

  public final ImmutableTable<RowKey, ColumnKey, Value> getTable() {
    ImmutableTable<RowKey, ColumnKey, Value> item = getItem();
    return item == null ? ImmutableTable.of() : item;
  }
}
