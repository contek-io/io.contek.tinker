package io.contek.tinker.rearm;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

@ThreadSafe
public abstract class RearmTableStore<RowKey, ColumnKey, Value>
    extends RearmStore<ImmutableTable<RowKey, ColumnKey, Value>> {

  protected RearmTableStore(Path configPath, IParser<RowKey, ColumnKey, Value> parser) {
    super(configPath, parser);
  }

  public final Value get(RowKey rowKey, ColumnKey columnKey) throws NoSuchElementException {
    return getOrThrow(rowKey, columnKey, NoSuchElementException::new);
  }

  public final <E extends Throwable> Value getOrThrow(
      RowKey rowKey, ColumnKey columnKey, Supplier<E> t) throws E {
    Value value = getNullable(rowKey, columnKey);
    if (value == null) {
      throw t.get();
    }
    return value;
  }

  @Nullable
  public final Value getNullable(RowKey rowKey, ColumnKey columnKey) {
    Table<RowKey, ColumnKey, Value> table = getTable();
    return table.get(rowKey, columnKey);
  }

  public final ImmutableTable<RowKey, ColumnKey, Value> getTable() {
    ImmutableTable<RowKey, ColumnKey, Value> item = getItem();
    return item == null ? ImmutableTable.of() : item;
  }

  /** Parser to read and parse {@link ImmutableTable} from a file. */
  @ThreadSafe
  public interface IParser<RowKey, ColumnKey, Value>
      extends RearmStore.IParser<ImmutableTable<RowKey, ColumnKey, Value>> {}

  /** Listener which gets called when {@link ImmutableTable} has update. */
  @ThreadSafe
  public interface IListener<RowKey, ColumnKey, Value>
      extends RearmStore.IListener<ImmutableTable<RowKey, ColumnKey, Value>> {}
}
