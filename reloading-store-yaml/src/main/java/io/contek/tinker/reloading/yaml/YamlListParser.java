package io.contek.tinker.reloading.yaml;

import com.google.common.collect.ImmutableList;
import io.contek.tinker.reloading.ReloadingListStore;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class YamlListParser<YamlType, Value>
    extends YamlParser<YamlType, ImmutableList<Value>>
    implements ReloadingListStore.IParser<Value> {

  public YamlListParser() {}

  public YamlListParser(Yaml yaml) {
    super(yaml);
  }
}
