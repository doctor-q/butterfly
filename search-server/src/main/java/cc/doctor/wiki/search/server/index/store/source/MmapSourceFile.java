package cc.doctor.wiki.search.server.index.store.source;

import cc.doctor.wiki.search.server.index.config.GlobalConfig;
import cc.doctor.wiki.utils.PropertyUtils;
import cc.doctor.wiki.search.server.index.store.mm.MmapFile;
import cc.doctor.wiki.utils.DateUtils;
import cc.doctor.wiki.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Created by doctor on 2017/3/8.
 */
public class MmapSourceFile extends SourceFile {
    private static final Logger log = LoggerFactory.getLogger(SourceFile.class);
    private static final int sourceFileSize = PropertyUtils.getProperty(GlobalConfig.SOURCE_FILE_SIZE_NAME, GlobalConfig.SOURCE_FILE_SIZE_DEFUALT);
    private MmapFile mmapFile;

    @Override
    public int appendSource(Source source) {
        try {
            byte[] bytes = SerializeUtils.serialize(source.getDocument());
            source.setSize(bytes.length);
            int position = mmapFile.getPosition();
            if (mmapFile.canAppend(bytes.length + 4 + 8)) {
                appendSource(source.getSize(), source.getVersion(), bytes);
                return position;
            } else {
                mmapFile.commit();
                mmapFile.clean();
                String newFileName = DateUtils.toYMDHMS(new Date());
                mmapFile = new MmapFile(newFileName, sourceFileSize);
                appendSource(source.getSize(), source.getVersion(), bytes);
                return 0;
            }
        } catch (IOException e) {
            log.error("", e);
        }
        return -1;
    }

    private void appendSource(int size, long version, byte[] bytes) {
        mmapFile.appendInt(size);
        mmapFile.appendLong(version);
        mmapFile.appendBytes(bytes);
    }
}
