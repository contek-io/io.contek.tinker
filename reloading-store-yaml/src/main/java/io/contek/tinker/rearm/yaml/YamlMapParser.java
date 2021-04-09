package io.contek.tinker.rearm.yaml;

import com.google.common.collect.ImmutableMap;
import io.contek.tinker.rearm.RearmMapStore;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlMapParser<YamlType, Key, Value>
    extends YamlParser<YamlType, ImmutableMap<Key, Value>>
    implements RearmMapStore.IParser<Key, Value> {}
