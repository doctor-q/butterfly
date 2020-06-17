package cc.doctor.search.store.source;

import cc.doctor.search.common.utils.PropertyUtils;
import cc.doctor.search.store.StoreConfigs;
import cc.doctor.search.store.mm.AppendFile;
import cc.doctor.search.store.mm.MmapScrollFile;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by doctor on 2017/3/8.
 */
@Slf4j
public class MmapSourceFile extends SourceFile {
    private static final int sourceFileSize = PropertyUtils.getProperty(StoreConfigs.SOURCE_FILE_SIZE_NAME, StoreConfigs.SOURCE_FILE_SIZE_DEFUALT);
    private AppendFile scrollFile;

    public MmapSourceFile() {
        scrollFile = new MmapScrollFile(sourceRoot, sourceFileSize);
    }

    public MmapSourceFile(String sourceRoot) {
        super(sourceRoot);
        scrollFile = new MmapScrollFile(sourceRoot, sourceFileSize);
    }

    @Override
    public long appendSource(Source source) {
        return scrollFile.writeObject(source);
    }

    @Override
    public Source getSource(long position) {
        return (Source) scrollFile.readObject(position, 0);
    }
}
