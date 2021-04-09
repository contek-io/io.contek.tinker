package io.contek.tinker.cli;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Console;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

@NotThreadSafe
public final class KeyboardInput {

  private final String question;
  private boolean hide = false;
  private String cache;

  public static KeyboardInput withQuestion(String question) {
    return new KeyboardInput(question);
  }

  public KeyboardInput setHide(boolean hide) {
    this.hide = hide;
    return this;
  }

  public Supplier<Integer> asIntSupplier() {
    return asSupplier(Integer::parseInt);
  }

  public Supplier<Path> asPathSupplier() {
    return asSupplier(Paths::get);
  }

  public Supplier<String> asSupplier() {
    return this::read;
  }

  public <T> Supplier<T> asSupplier(Function<String, T> converter) {
    return () -> converter.apply(read());
  }

  public int readInt() {
    return Integer.parseInt(read());
  }

  public Path readPath() {
    return Paths.get(read());
  }

  public String read() {
    if (cache != null) {
      return cache;
    }
    if (hide) {
      Console console = System.console();
      if (console != null) {
        return new String(console.readPassword(question));
      }
    }

    System.out.println(question);
    Scanner scan = new Scanner(System.in);
    return cache = scan.nextLine();
  }

  private KeyboardInput(String question) {
    this.question = question;
  }
}
