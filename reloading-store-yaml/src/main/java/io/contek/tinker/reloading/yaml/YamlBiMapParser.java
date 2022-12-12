package io.contek.tinker.reloading.yaml;

import com.google.common.collect.ImmutableBiMap;
import io.contek.tinker.reloading.ReloadingBiMapStore;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlBiMapParser<YamlType, Key, Value>
    extends YamlParser<YamlType, ImmutableBiMap<Key, Value>>
    implements ReloadingBiMapStore.IParser<Key, Value> {

  public YamlBiMapParser() {}

  public YamlBiMapParser(Yaml yaml) {
    super(yaml);
  }
}
