package io.contek.tinker.rearm.yaml;

import io.contek.tinker.rearm.RearmStore;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import org.yaml.snakeyaml.Yaml;

@ThreadSafe
public abstract class YamlParser<YamlType, ParsedConfig>
    implements RearmStore.IParser<ParsedConfig> {

  @Override
  public final ParsedConfig parse(Path path) throws IOException {
    YamlType yaml;
    try (InputStream input = Files.newInputStream(path)) {
      yaml = new Yaml().loadAs(input, getYamlType());
    }
    return parse(path, yaml);
  }

  /**
   * @return the raw Yaml file type.
   */
  protected abstract Class<YamlType> getYamlType();

  /**
   * Parses the given Yaml file.
   *
   * @param path the path of the Yaml file.
   * @param yaml the raw Yaml file. {@code null} if it is empty.
   * @return the parsing result.
   */
  protected abstract ParsedConfig parse(Path path, @Nullable YamlType yaml);
}
