package io.contek.tinker.reloading;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ReloadingStoreAlreadyStartedException extends IllegalStateException {

  ReloadingStoreAlreadyStartedException() {
  }
}
