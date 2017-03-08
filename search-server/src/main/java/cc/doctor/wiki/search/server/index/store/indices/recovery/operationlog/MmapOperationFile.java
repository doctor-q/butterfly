package cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog;

import cc.doctor.wiki.search.server.index.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.config.PropertyUtils;
import cc.doctor.wiki.search.server.index.store.mm.MmapFile;
import cc.doctor.wiki.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Created by doctor on 2017/3/8.
 */
public class MmapOperationFile extends OperationLogFile {
    private static final Logger log = LoggerFactory.getLogger(OperationLogFile.class);
    private static final int operationLogSize = PropertyUtils.getProperty(GlobalConfig.OPERATION_LOG_SIZE_NAME, GlobalConfig.OPERATION_LOG_SIZE_DEFAULT);
    private MmapFile mmapFile;

    @Override
    public boolean appendOperationLogInner(OperationLog operationLog) {
        if (mmapFile.canAppend(4 + 4 + 8 + operationLog.getData().length)) {
            appendLog(operationLog);
        } else {
            mmapFile.commit();
            mmapFile.clean();
            String newFileName = DateUtils.toYMDHMS(new Date()) + ".op";
            try {
                mmapFile = new MmapFile(newFileName, operationLogSize);
                appendLog(operationLog);
            } catch (IOException e) {
                log.error("", e);
                return false;
            }
        }

        return true;
    }

    private void appendLog(OperationLog operationLog) {
        mmapFile.appendInt(operationLog.getSize());
        mmapFile.appendInt(operationLog.getOperation().getCode());
        mmapFile.appendLong(operationLog.getTimestamp());
        mmapFile.appendBytes(operationLog.getData());
    }
}
