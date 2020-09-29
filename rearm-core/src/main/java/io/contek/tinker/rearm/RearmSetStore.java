package io.contek.tinker.rearm;

import com.google.common.collect.ImmutableSet;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;

@ThreadSafe
public abstract class RearmSetStore<Value> extends RearmStore<ImmutableSet<Value>> {

  protected RearmSetStore(Path configPath, IParser<Value> parser) {
    super(configPath, parser);
  }

  public final ImmutableSet<Value> getSet() {
    ImmutableSet<Value> item = getItem();
    return item == null ? ImmutableSet.of() : item;
  }

  /** Parser to read and parse {@link ImmutableSet} from a file. */
  @ThreadSafe
  public interface IParser<Value> extends RearmStore.IParser<ImmutableSet<Value>> {}

  /** Listener which gets called when {@link RearmSetStore} has update. */
  @ThreadSafe
  public interface IListener<Value> extends RearmStore.IListener<ImmutableSet<Value>> {}
}
