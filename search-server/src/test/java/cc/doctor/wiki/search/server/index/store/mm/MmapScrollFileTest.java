package cc.doctor.wiki.search.server.index.store.mm;

import org.junit.Test;

/**
 * Created by doctor on 2017/3/16.
 */
public class MmapScrollFileTest {
    @Test
    public void writeSerializable() throws Exception {
        MmapScrollFile mmapScrollFile = new MmapScrollFile("/tmp/test/scoll/",1000, new ScrollFile.ScrollFileNameStrategy() {
            @Override
            public String first() {
                return "/tmp/test/scoll/1";
            }

            @Override
            public String next(String current) {
                String file = current.substring(current.lastIndexOf("/") + 1);
                return current.substring(0, current.lastIndexOf("/") + 1) + String.valueOf((Integer.parseInt(file) + 1));
            }
        });


        for (int i = 0; i < 10000; i++) {
            mmapScrollFile.writeSerializable(1L);
        }
    }

}