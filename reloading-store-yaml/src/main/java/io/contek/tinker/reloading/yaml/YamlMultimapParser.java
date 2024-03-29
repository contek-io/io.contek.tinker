package io.contek.tinker.reloading.yaml;

import com.google.common.collect.ImmutableMultimap;
import io.contek.tinker.reloading.ReloadingMultimapStore;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlMultimapParser<YamlType, Key, Value>
    extends YamlParser<YamlType, ImmutableMultimap<Key, Value>>
    implements ReloadingMultimapStore.IParser<Key, Value> {

  public YamlMultimapParser() {}

  public YamlMultimapParser(Yaml yaml) {
    super(yaml);
  }
}
