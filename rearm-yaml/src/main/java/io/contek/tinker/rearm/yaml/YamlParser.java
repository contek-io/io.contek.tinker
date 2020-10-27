package io.contek.tinker.rearm.yaml;

import io.contek.tinker.rearm.RearmStore;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@ThreadSafe
public abstract class YamlParser<YamlType, Item> implements RearmStore.IParser<Item> {

  @Override
  public final Item parse(Path path) throws IOException {
    YamlType yaml;
    try (InputStream input = Files.newInputStream(path)) {
      yaml = new Yaml().loadAs(input, getYamlType());
    }
    return parse(yaml);
  }

  protected abstract Class<YamlType> getYamlType();

  protected abstract Item parse(YamlType yaml);
}