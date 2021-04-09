package io.contek.tinker.rearm;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import java.nio.file.Path;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ReloadingMultimapStore<Key, Value>
    extends ReloadingStore<ImmutableMultimap<Key, Value>> {

  protected ReloadingMultimapStore(Path configPath, IParser<Key, Value> parser) {
    super(configPath, parser);
  }

  public final ImmutableCollection<Value> get(Key key) {
    ImmutableMultimap<Key, Value> multimap = getMultimap();
    return multimap.get(key);
  }

  public final ImmutableMultimap<Key, Value> getMultimap() {
    ImmutableMultimap<Key, Value> item = getParsedConfig();
    return item == null ? ImmutableMultimap.of() : item;
  }

  /** Parser to read and parse {@link ImmutableMultimap} from a file. */
  @ThreadSafe
  public interface IParser<Key, Value> extends ReloadingStore.IParser<ImmutableMultimap<Key, Value>> {}

  /** Listener which gets called when {@link ReloadingMultimapStore} has update. */
  @ThreadSafe
  public interface IListener<Key, Value>
      extends ReloadingStore.IListener<ImmutableMultimap<Key, Value>> {}
}
