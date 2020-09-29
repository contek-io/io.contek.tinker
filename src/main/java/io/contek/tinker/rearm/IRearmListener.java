package io.contek.tinker.rearm;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;
import java.time.Instant;

/**
 * Listener which gets called when {@link RearmStore} has update.
 */
@ThreadSafe
public interface IRearmListener<Item> extends Comparable<IRearmListener<?>> {

  /**
   * Called when an error occurs.
   *
   * @param t the error.
   */
  void onError(Throwable t);

  /**
   * Called when the stored value has changed.
   *
   * @param path         the path of the yaml file.
   * @param newValue     the new value.
   * @param oldValue     the old value.
   * @param modifiedTime the modified time of the yaml file.
   */
  void onRearm(Path path, Item newValue, @Nullable Item oldValue, Instant modifiedTime);

  /**
   * The priority of this listener. A listener with lower value returned from this method will get
   * called earlier.
   *
   * @return the score that reflects the priority of this listener.
   */
  default int getPriority() {
    return 0;
  }

  @Override
  default int compareTo(IRearmListener<?> that) {
    return Integer.compare(this.getPriority(), that.getPriority());
  }
}
