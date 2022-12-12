package io.contek.tinker.reloading.yaml;

import com.google.common.collect.ImmutableSet;
import io.contek.tinker.reloading.ReloadingSetStore;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlSetParser<YamlType, Value>
    extends YamlParser<YamlType, ImmutableSet<Value>> implements ReloadingSetStore.IParser<Value> {
  public YamlSetParser() {}

  public YamlSetParser(Yaml yaml) {
    super(yaml);
  }
}
