package io.contek.tinker.rearm.yaml;

import com.google.common.collect.ImmutableMultimap;
import io.contek.tinker.rearm.RearmMultimapStore;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlMultimapParser<YamlType, Key, Value>
    extends YamlParser<YamlType, ImmutableMultimap<Key, Value>>
    implements RearmMultimapStore.IParser<Key, Value> {}
