package cc.doctor.wiki.search.server.index.store.indices.recovery;

import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.CheckPointFile;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.MmapOperationFile;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.OperationLog;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.OperationLogFile;
import cc.doctor.wiki.search.server.index.shard.ShardService;

import java.io.IOException;

/**
 * Created by doctor on 2017/3/9.
 * 数据恢复服务,负责操作日志的管理和索引的恢复
 */
public class RecoveryService {
    private ShardService shardService;
    private OperationLogFile operationLogFile;  //操作日志目录
    private CheckPointFile checkPointFile;
    private String operationLogPath;

    public RecoveryService(ShardService shardService) {
        this.shardService = shardService;
        operationLogPath = shardService.getShardRoot() + "/" + GlobalConfig.OPERATION_LOG_PATH_NAME;
        checkPointFile = new CheckPointFile();
        try {
            operationLogFile = new MmapOperationFile(checkPointFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getOperationLogPath() {
        return operationLogPath;
    }

    public CheckPointFile getCheckPointFile() {
        return checkPointFile;
    }

    public boolean appendOperationLog(OperationLog operationLog) {
        return operationLogFile.appendOperationLog(operationLog);
    }

    public Long getCheckPoint() {
        return checkPointFile.getCheckPoint();
    }

    public void doRecovery() {

    }
}
