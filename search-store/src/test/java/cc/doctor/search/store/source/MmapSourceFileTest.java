package cc.doctor.search.store.source;

import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.document.Field;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

public class MmapSourceFileTest {
    private SourceFile sourceFile;

    @Before
    public void setUp() {
        sourceFile = new MmapSourceFile("/tmp/mmapsource");
    }

    @org.junit.Test
    public void appendSource() {
        Source source = new Source();
        Document doc = new Document();
        doc.setId(1L);
        List<Field> fields = new ArrayList<>();
        fields.add(new Field("name", "cc"));
        doc.setFields(fields);
        source.setDocument(doc);
        for (int i = 0; i < 100; i++) {
            long l = sourceFile.appendSource(source);
            System.out.println(l);
        }
    }

    @org.junit.Test
    public void getSource() {
        /**
         * 47337
         * 47846
         * 48355
         * 48864
         * 49373
         * 49882
         * 50391
         */
        Source source = sourceFile.getSource(47337);
        System.out.println(source);
    }
}