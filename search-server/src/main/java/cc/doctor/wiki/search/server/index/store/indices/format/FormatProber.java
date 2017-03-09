package cc.doctor.wiki.search.server.index.store.indices.format;

import java.util.Date;

/**
 * Created by doctor on 2017/3/3.
 */
public class FormatProber {

    public static Format proberFormat (Object data) {
        if (data == null) {
            return null;
        }
        String word = data.toString();
        try {
            long parseLong = Long.parseLong(word);
            return Format.LONG;
        } catch (Exception ignore) {
        }
        try {
            double parseDouble = Double.parseDouble(word);
            return Format.DOUBLE;
        } catch (Exception ignore) {
        }
        try {
            Date date = DateFormat.proberDate(word);
            if (date != null) {
                return Format.DATE;
            }
        }catch (Exception ignore) {
        }
        return Format.STRING;
    }
}
