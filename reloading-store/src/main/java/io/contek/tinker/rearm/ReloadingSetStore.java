package io.contek.tinker.rearm;

import com.google.common.collect.ImmutableSet;
import java.nio.file.Path;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ReloadingSetStore<Value> extends ReloadingStore<ImmutableSet<Value>> {

  protected ReloadingSetStore(Path configPath, IParser<Value> parser) {
    super(configPath, parser);
  }

  public final ImmutableSet<Value> getSet() {
    ImmutableSet<Value> item = getParsedConfig();
    return item == null ? ImmutableSet.of() : item;
  }

  /** Parser to read and parse {@link ImmutableSet} from a file. */
  @ThreadSafe
  public interface IParser<Value> extends ReloadingStore.IParser<ImmutableSet<Value>> {}

  /** Listener which gets called when {@link ReloadingSetStore} has update. */
  @ThreadSafe
  public interface IListener<Value> extends ReloadingStore.IListener<ImmutableSet<Value>> {}
}
