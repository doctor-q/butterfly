package cc.doctor.search.store.indices.format;

/**
 * Created by doctor on 2017/3/3.
 */
public enum Format {
    //// TODO: 2017/3/9 BigDecimal
    STRING("string"),
    LONG("long") {
        @Override
        public Object format(String value) {
            return Long.parseLong(value);
        }
    },
    DATE("date"),
    DOUBLE("double") {
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

    public static Format getFormat(String name) {
        if (name == null) {
            return null;
        }
        for (Format format : Format.values()) {
            if (format.name.equals(name)) {
                return format;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
