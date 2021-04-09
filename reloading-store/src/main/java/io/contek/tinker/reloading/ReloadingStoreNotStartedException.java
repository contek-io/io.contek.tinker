package io.contek.tinker.reloading;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ReloadingStoreNotStartedException extends IllegalStateException {

  ReloadingStoreNotStartedException() {
  }
}
