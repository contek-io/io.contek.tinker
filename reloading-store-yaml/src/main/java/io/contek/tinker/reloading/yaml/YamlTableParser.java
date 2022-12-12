package io.contek.tinker.reloading.yaml;

import com.google.common.collect.ImmutableTable;
import io.contek.tinker.reloading.ReloadingTableStore;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlTableParser<YamlType, RowKey, ColumnKey, Value>
    extends YamlParser<YamlType, ImmutableTable<RowKey, ColumnKey, Value>>
    implements ReloadingTableStore.IParser<RowKey, ColumnKey, Value> {

  public YamlTableParser() {}

  public YamlTableParser(Yaml yaml) {
    super(yaml);
  }
}
