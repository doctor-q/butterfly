package cc.doctor.search.server.index.store.mm.source;

import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.server.index.store.mm.MmapScrollFile;
import cc.doctor.search.server.index.shard.ShardService;
import cc.doctor.search.server.index.store.mm.ScrollFile;
import cc.doctor.search.common.utils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by doctor on 2017/3/8.
 */
public class MmapSourceFile extends SourceFile {
    private static final Logger log = LoggerFactory.getLogger(SourceFile.class);
    private static final int sourceFileSize = PropertyUtils.getProperty(GlobalConfig.SOURCE_FILE_SIZE_NAME, GlobalConfig.SOURCE_FILE_SIZE_DEFUALT);
    private ScrollFile scrollFile;

    public MmapSourceFile(ShardService shardService) {
        super(shardService);
        scrollFile = new MmapScrollFile(sourceRoot, sourceFileSize);
    }

    @Override
    public long appendSource(Source source) {
        return scrollFile.writeSerializable(source);
    }

    @Override
    public Source getSource(long position) {
        return (Source) scrollFile.readSerializable(position).getT2();
    }
}
