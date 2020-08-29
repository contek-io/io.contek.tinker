package io.contek.reloading;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.isRegularFile;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import org.yaml.snakeyaml.Yaml;

@ThreadSafe
public abstract class ReloadingStore<Config, Item> {

  private final Path configPath;
  private final Class<Config> configType;

  private final AtomicBoolean started = new AtomicBoolean(false);
  private final AtomicReference<Instant> modifiedTimeHolder = new AtomicReference<>(null);
  private final AtomicReference<Item> itemHolder = new AtomicReference<>(null);

  private Duration initialDelay = Duration.ZERO;
  private Duration delay = Duration.ofSeconds(10);

  protected ReloadingStore(Path configPath, Class<Config> configType) {
    this.configPath = configPath;
    this.configType = configType;
  }

  public void setInitialDelay(Duration initialDelay) {
    synchronized (started) {
      if (started.get()) {
        throw new IllegalStateException();
      }
      this.initialDelay = initialDelay;
    }
  }

  public void setDelay(Duration delay) {
    synchronized (started) {
      if (started.get()) {
        throw new IllegalStateException();
      }
      this.delay = delay;
    }
  }

  @CanIgnoreReturnValue
  public final ListenableFuture<?> start() {
    synchronized (started) {
      if (started.getAndSet(true)) {
        throw new IllegalStateException();
      }
      return listeningDecorator(newSingleThreadScheduledExecutor()).scheduleWithFixedDelay(
          this::checkAndReloadProfiles, initialDelay, delay);
    }
  }

  @Nullable
  public final Item getItem() {
    synchronized (started) {
      if (!started.get()) {
        throw new IllegalStateException();
      }
    }
    synchronized (itemHolder) {
      return itemHolder.get();
    }
  }

  protected abstract Item read(Config config);

  protected abstract void onError(Throwable t);

  protected abstract void onUpdate(Item newValue, @Nullable Item oldValue, Instant modifiedTime);

  private void checkAndReloadProfiles() {
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
                newItem = itemHolder.updateAndGet(
                    oldValue -> {
                      Config config;
                      try (InputStream stream = Files.newInputStream(configPath)) {
                        config = new Yaml().loadAs(stream, configType);
                      } catch (IOException e) {
                        errorHolder.set(e);
                        return oldValue;
                      }
                      return read(config);
                    });
                if (errorHolder.get() != null) {
                  onError(errorHolder.get());
                  return oldModifiedTime;
                }
              } catch (Throwable t) {
                onError(t);
                return oldModifiedTime;
              }
              onUpdate(newItem, oldItem, newModifiedTime);
              return newModifiedTime;
            }
          });
    }
  }
}
