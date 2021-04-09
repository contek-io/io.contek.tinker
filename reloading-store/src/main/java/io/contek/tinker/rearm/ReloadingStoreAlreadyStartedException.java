package io.contek.tinker.rearm;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ReloadingStoreAlreadyStartedException extends IllegalStateException {

  ReloadingStoreAlreadyStartedException() {
  }
}
