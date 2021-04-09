package io.contek.tinker.rearm.yaml;

import com.google.common.collect.ImmutableBiMap;
import io.contek.tinker.rearm.ReloadingBiMapStore;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlBiMapParser<YamlType, Key, Value>
    extends YamlParser<YamlType, ImmutableBiMap<Key, Value>>
    implements ReloadingBiMapStore.IParser<Key, Value> {}
