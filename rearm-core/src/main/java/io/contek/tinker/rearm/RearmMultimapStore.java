package io.contek.tinker.rearm;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;

@ThreadSafe
public abstract class RearmMultimapStore<Key, Value>
    extends RearmStore<ImmutableMultimap<Key, Value>> {

  protected RearmMultimapStore(Path configPath, IParser<Key, Value> parser) {
    super(configPath, parser);
  }

  public final ImmutableCollection<Value> get(Key key) {
    ImmutableMultimap<Key, Value> multimap = getMultimap();
    return multimap.get(key);
  }

  public final ImmutableMultimap<Key, Value> getMultimap() {
    ImmutableMultimap<Key, Value> item = getItem();
    return item == null ? ImmutableMultimap.of() : item;
  }

  /** Parser to read and parse {@link ImmutableMultimap} from a file. */
  @ThreadSafe
  public interface IParser<Key, Value> extends RearmStore.IParser<ImmutableMultimap<Key, Value>> {}

  /** Listener which gets called when {@link RearmMultimapStore} has update. */
  @ThreadSafe
  public interface IListener<Key, Value>
      extends RearmStore.IListener<ImmutableMultimap<Key, Value>> {}
}
