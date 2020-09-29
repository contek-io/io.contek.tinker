package io.contek.tinker.rearm.yaml;

import com.google.common.collect.ImmutableSet;
import io.contek.tinker.rearm.RearmSetStore;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlSetParser<YamlType, Value>
    extends YamlParser<YamlType, ImmutableSet<Value>> implements RearmSetStore.IParser<Value> {}
