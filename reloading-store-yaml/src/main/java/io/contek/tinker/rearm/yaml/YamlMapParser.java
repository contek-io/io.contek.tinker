package io.contek.tinker.rearm.yaml;

import com.google.common.collect.ImmutableMap;
import io.contek.tinker.rearm.ReloadingMapStore;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlMapParser<YamlType, Key, Value>
    extends YamlParser<YamlType, ImmutableMap<Key, Value>>
    implements ReloadingMapStore.IParser<Key, Value> {}
