package cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog;

import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.store.mm.MmapFile;
import cc.doctor.wiki.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by doctor on 2017/3/8.
 * 检查点设置文件,检查点后的文件全部建立索引,从检查点开始恢复
 * checkpoint file first, then operation log
 */
public class CheckPointFile {
    private static final Logger log = LoggerFactory.getLogger(CheckPointFile.class);
    private MmapFile mmapFile;
    private String checkpointFilePath;
    private Long checkpoint;

    public CheckPointFile() {
        checkpointFilePath = OperationLogFile.operationRoot + "/" + GlobalConfig.CHECKPOINT_FILE_NAME;
        try {
            if (!FileUtils.exists(checkpointFilePath)) {
                FileUtils.createFileRecursion(checkpointFilePath);
                mmapFile = new MmapFile(checkpointFilePath, 8);
                mmapFile.appendLong(0);
            } else {
                mmapFile = new MmapFile(checkpointFilePath, 8);
            }
            checkpoint = mmapFile.readLong(0);
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    public Long getCheckPoint() {
        return checkpoint;
    }
}
