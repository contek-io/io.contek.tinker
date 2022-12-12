package io.contek.tinker.reloading.yaml;

import com.google.common.collect.ImmutableMap;
import io.contek.tinker.reloading.ReloadingMapStore;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlMapParser<YamlType, Key, Value>
    extends YamlParser<YamlType, ImmutableMap<Key, Value>>
    implements ReloadingMapStore.IParser<Key, Value> {

  public YamlMapParser() {}

  public YamlMapParser(Yaml yaml) {
    super(yaml);
  }
}
