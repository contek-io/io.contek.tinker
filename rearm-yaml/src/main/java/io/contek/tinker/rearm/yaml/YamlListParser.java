package io.contek.tinker.rearm.yaml;

import com.google.common.collect.ImmutableList;
import io.contek.tinker.rearm.RearmListStore;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlListParser<YamlType, Value>
    extends YamlParser<YamlType, ImmutableList<Value>> implements RearmListStore.IParser<Value> {}
