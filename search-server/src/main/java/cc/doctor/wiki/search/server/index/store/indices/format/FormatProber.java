package cc.doctor.wiki.search.server.index.store.indices.format;

import cc.doctor.wiki.search.server.index.store.schema.Schema;

/**
 * Created by doctor on 2017/3/3.
 */
public class FormatProber {

    public static Number toNumber(Schema.Property property, Object word) {
        if (word == null) {
            return null;
        }
        if (property.getType() != null) {
            for (Format format : Format.values()) {
                if (!(format.equals(Format.DATE) || format.equals(Format.STRING))) {
                    format.format(word.toString());
                }
            }
        } else {
            try {
                long parseLong = Long.parseLong(word.toString());
                property.setType("long");
                return parseLong;
            } catch (Exception ignore) {
            }
            try {
                double parseDouble = Double.parseDouble(word.toString());
                property.setType("double");
                return parseDouble;
            } catch (Exception ignore) {
            }

            try {
                return DateFormat.propeAndTransfer(property, word);
            } catch (Exception ignore) {
            }
        }
        return null;
    }
}
