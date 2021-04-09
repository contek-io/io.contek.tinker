package io.contek.tinker.rearm.yaml;

import com.google.common.collect.ImmutableSet;
import io.contek.tinker.rearm.ReloadingSetStore;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlSetParser<YamlType, Value>
    extends YamlParser<YamlType, ImmutableSet<Value>> implements ReloadingSetStore.IParser<Value> {}
