package cc.doctor.wiki.search.server.index.store.indices.format;

/**
 * Created by doctor on 2017/3/3.
 */
public enum Format {
    STRING("string"), LONG("long") {
        @Override
        public Object format(String value) {
            return Long.parseLong(value);
        }
    }, DATE("date"), DOUBLE("double") {
        @Override
        public Object format(String value) {
            return Double.parseDouble(value);
        }
    }, BIG_DECIMAL("big_decimal") {
        @Override
        public Object format(String value) {
            return Double.parseDouble(value);
        }
    };

    String name;

    Format(String name) {
        this.name = name;
    }

    public Object format(String value) {
        return value;
    }
}
