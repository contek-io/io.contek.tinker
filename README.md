# io.contek.tinker
A Java library to automatically reload and cache configs.

## Maven
``` xml
<dependency>
    <groupId>io.contek.tinker</groupId>
    <artifactId>rearm-core</artifactId>
    <version>1.1.0</version>
</dependency>

<dependency>
    <groupId>io.contek.invoker</groupId>
    <artifactId>rearm-yaml</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Example
1) Declare Yaml config file structure
    ``` java
     /**
      * Yaml file structure. Good practices:
      * <li>Snake case
      * <li>Public field
      * <li>No constructor
      * <li>No method
      * <li>Use primitives only
      * <li>Specify unit in field name
      */
      public final class YamlFile {
          public int int_value;
          public String string_value;
          public int duration_in_seconds;
      }
    ```
2) Create parsed config POJO class
    ``` java
    /**
     * Parsed config file. Good practices:
     * <li>Camel case (like normal java code)
     * <li>Private Fields
     * <li>Strictly immutable
     * <li>Get methods only
     * <li>Use sophisticated classes (Enum, Instant, Duration etc)
     */
    public final class ParsedConfig {
        private final int intValue;
        private final String stringValue;
        private final Duration durationValue;
        
        public ParsedConfig(int intValue, String stringValue, Duration durationValue) {
            this.intValue = intValue;
            this.stringValue = stringValue;
            this.durationValue = durationValue;
        }
        
        public int getIntValue() {
            return intValue;
        }
        
        public String getStringValue() {
            return stringValue;
        }
        
        public Duration getDurationValue() {
            return durationValue;
        }
    }
    ```
3) Create parser
    ``` java
    /**
     * Parser to create parsed config from Yaml file. Good practices:
     * <li>Stateless
     */
    public final class Parser extends YamlParser<YamlFile, ParsedConfig> {
    
        @Override
        protected Class<YamlFile> getYamlType() {
            return YamlFile.class;
        }
    
        @Override
        protected ParsedConfig parse(YamlFile yaml) {
            return new ParsedConfig(yaml.int_value, yaml.string_value, durationValue);
        }
    }
    ```
4) Create store
    ``` java
    /**
     * Store to cache parsed config. Good practices:
     * <li>No field
     * <li>Log on state change
     * <li>No set method
     */
    public final class ReamStoreExample extends RearmStore<ParsedConfig>
        implements RearmStore.IListener<ParsedConfig> {
    
        public ReamStoreExample(Path configPath) {
            super(configPath, new Parser());
             addListener(this);
             start();
        }
    
        @Nullable
        public Instant getExpiry() {
            ParsedConfig config = getItem();
            if (config == null) {
                return null;
            }
            return Instant.now().plus(config.getDurationValue());
        }
    
        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }
    
        @Override
        public void onRearm(
                Path path, ParsedConfig newValue, @Nullable ParsedConfig oldValue, Instant modifiedTime) {
            System.out.println("New int value is " + newValue.getIntValue());
            System.out.println("New string value is " + newValue.getStringValue());
        }
    }
    ```
5) Use the store
``` java
public class Example {
    private final ReamStoreExample store;
    private final BlockingQueue<Instant> queue;
    
    public Example(ReamStoreExample store, BlockingQueue<Instant> queue) {
        this.store = store;
        this.queue = queue;
    }

    public void checkNextAgainstExpiry() throws InterruptedException {
        Instant expiry = store.getExpiry();
        if (expiry == null) {
            System.out.println("No expiry information");
            return;
        }
        Instant next = queue.take();
        if (next.isBefore(expiry)) {
            System.out.println(next + " is OK");
        } else {
            System.err.println(next + " is outdated");
        }
    }
}
```

## Additional templates
This library also provides other types of store templates to help you with configs which use common data types:
* `RearmBiMapStore` and `YamlBiMapParser`
* `RearmListStore` and `YamlListParser`
* `RearmMapStore` and `YamlMapParser`
* `RearmMultimapStore` and `YamlMultimapParser`
* `RearmSetStore` and `YamlSetParser`
* `RearmTableStore` and `YamlTableParser`

