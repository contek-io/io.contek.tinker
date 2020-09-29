package io.contek.tinker.rearm;

import com.google.common.util.concurrent.ListenableFuture;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.isRegularFile;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/**
 * Templates to build stores which will reload automatically and periodically.
 *
 * @param <Config> the class that reflects the structure of the yaml file.
 * @param <Item>   the class to store contents from parsing the yaml file.
 */
@ThreadSafe
public abstract class RearmStore<Config, Item> {

  private final Path configPath;
  private final Class<Config> configType;

  private final AtomicReference<ListenableFuture<?>> started = new AtomicReference<>(null);
  private final AtomicReference<Instant> modifiedTimeHolder = new AtomicReference<>(null);
  private final AtomicReference<Item> itemHolder = new AtomicReference<>(null);

  private Duration initialDelay = Duration.ZERO;
  private Duration delay = Duration.ofSeconds(10);
  private final List<IListener<? super Item>> listeners = new LinkedList<>();

  protected RearmStore(Path configPath, Class<Config> configType) {
    this.configPath = configPath;
    this.configType = configType;
  }

  /**
   * Sets the delay before the first {@link #checkAndReload()} call.
   *
   * @param initialDelay the delay before the first {@link #checkAndReload()} call.
   * @return this store.
   * @throws IllegalArgumentException if the input is not positive.
   * @throws RearmStoreAlreadyStartedException if the store has already started.
   */
  public RearmStore<Config, Item> setInitialDelay(Duration initialDelay)
      throws IllegalArgumentException, RearmStoreAlreadyStartedException {
    if (delay.isNegative()) {
      throw new IllegalArgumentException(delay.toString());
    }

    synchronized (started) {
      if (started.get() != null) {
        throw new RearmStoreNotStartedException();
      }
      this.initialDelay = initialDelay;
    }
    return this;
  }

  /**
   * Sets the interval between two consecutive {@link #checkAndReload()} calls.
   *
   * @param delay the interval between two consecutive {@link #checkAndReload()} calls.
   * @return this store.
   * @throws IllegalArgumentException if the put is not positive.
   * @throws RearmStoreAlreadyStartedException if the store has already started.
   */
  public RearmStore<Config, Item> setDelay(Duration delay)
      throws IllegalArgumentException, RearmStoreAlreadyStartedException {
    if (delay.isZero() || delay.isNegative()) {
      throw new IllegalArgumentException(delay.toString());
    }

    synchronized (started) {
      if (started.get() != null) {
        throw new RearmStoreNotStartedException();
      }
      this.delay = delay;
    }
    return this;
  }

  /**
   * Adds the given listener to {@link #listeners}.
   */
  public RearmStore<Config, Item> addListener(IListener<? super Item> listener) {
    synchronized (listeners) {
      listeners.add(listener);
      Collections.sort(listeners);
    }
    return this;
  }

  /**
   * Removes the given listener from {@link #listeners}.
   */
  public RearmStore<Config, Item> removeListener(IListener<? super Item> listener) {
    synchronized (listeners) {
      listeners.remove(listener);
    }
    return this;
  }

  /**
   * Starts this store, which will periodically parse the file at {@link #configPath} and store the
   * parsing result.
   *
   * @throws RearmStoreAlreadyStartedException if this store has already started.
   */
  public final void start() throws RearmStoreAlreadyStartedException {
    synchronized (started) {
      started.updateAndGet(
          oldValue -> {
            if (oldValue != null && !oldValue.isDone()) {
              throw new RearmStoreAlreadyStartedException();
            }
            return listeningDecorator(newSingleThreadScheduledExecutor())
                .scheduleWithFixedDelay(this::checkAndReload, initialDelay, delay);
          });
    }
  }

  /** Stops this store. No effect if this store is not started. */
  public final void stop() {
    synchronized (started) {
      started.updateAndGet(
          oldValue -> {
            if (oldValue != null) {
              oldValue.cancel(true);
            }
            return null;
          });
    }
  }

  /**
   * Returns the latest stored value.
   *
   * @return the latest stored value. {@code null} if no value is stored.
   * @throws RearmStoreNotStartedException if the store is not started.
   */
  @Nullable
  public final Item getItem() throws RearmStoreNotStartedException {
    synchronized (started) {
      if (started.get() == null) {
        throw new RearmStoreNotStartedException();
      }
    }
    synchronized (itemHolder) {
      return itemHolder.get();
    }
  }

  /**
   * Parses the given yaml file and returns the parsing result.
   *
   * @param path the path of the yaml file.
   * @param config the raw content of the yaml file. {@code null} if this file does not exist or is empty.
   * @return the item from parsing the yaml file.
   */
  protected abstract Item parse(Path path, @Nullable Config config);

  private void checkAndReload() {
    synchronized (modifiedTimeHolder) {
      modifiedTimeHolder.updateAndGet(
          oldModifiedTime -> {
            if (!isRegularFile(configPath)) {
              return oldModifiedTime;
            }
            Instant newModifiedTime;
            try {
              newModifiedTime = getLastModifiedTime(configPath).toInstant();
            } catch (IOException e) {
              onError(e);
              return oldModifiedTime;
            }

            if (oldModifiedTime != null && !newModifiedTime.isAfter(oldModifiedTime)) {
              return oldModifiedTime;
            }

            synchronized (itemHolder) {
              Item oldItem = itemHolder.get();
              Item newItem;
              AtomicReference<IOException> errorHolder = new AtomicReference<>(null);
              try {
                newItem =
                    itemHolder.updateAndGet(
                        oldValue -> {
                          Config config;
                          try (InputStream stream = Files.newInputStream(configPath)) {
                            config = new Yaml().loadAs(stream, configType);
                          } catch (IOException e) {
                            errorHolder.set(e);
                            return oldValue;
                          }
                          return parse(configPath, config);
                        });
                if (errorHolder.get() != null) {
                  onError(errorHolder.get());
                  return oldModifiedTime;
                }
              } catch (Throwable t) {
                onError(t);
                return oldModifiedTime;
              }
              onRearm(newItem, oldItem, newModifiedTime);
              return newModifiedTime;
            }
          });
    }
  }

  private void onError(Throwable t) {
    synchronized (listeners) {
      listeners.forEach(l -> l.onError(t));
    }
  }

  private void onRearm(Item newValue, @Nullable Item oldValue, Instant modifiedTime) {
    synchronized (listeners) {
      listeners.forEach(l -> l.onRearm(configPath, newValue, oldValue, modifiedTime));
    }
  }

  /**
   * Listener which gets called when {@link RearmStore} has update.
   */
  @ThreadSafe
  public interface IListener<Item> extends Comparable<IListener<?>> {

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
    default int compareTo(IListener<?> that) {
      return Integer.compare(this.getPriority(), that.getPriority());
    }
  }
}
