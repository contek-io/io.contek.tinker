package io.contek.tinker.rearm.yaml;

import com.google.common.collect.ImmutableTable;
import io.contek.tinker.rearm.RearmTableStore;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlTableParser<YamlType, RowKey, ColumnKey, Value>
    extends YamlParser<YamlType, ImmutableTable<RowKey, ColumnKey, Value>>
    implements RearmTableStore.IParser<RowKey, ColumnKey, Value> {}
