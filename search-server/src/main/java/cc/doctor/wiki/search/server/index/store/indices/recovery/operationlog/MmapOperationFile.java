package cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog;

import cc.doctor.wiki.exceptions.index.NoCheckPointException;
import cc.doctor.wiki.operation.Operation;
import cc.doctor.wiki.search.server.index.config.GlobalConfig;
import cc.doctor.wiki.utils.PropertyUtils;
import cc.doctor.wiki.search.server.index.store.indices.recovery.RecoveryService;
import cc.doctor.wiki.search.server.index.store.mm.MmapFile;
import cc.doctor.wiki.utils.DateUtils;
import cc.doctor.wiki.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by doctor on 2017/3/8.
 */
public class MmapOperationFile extends OperationLogFile {
    private static final Logger log = LoggerFactory.getLogger(OperationLogFile.class);
    private static final int operationLogSize = PropertyUtils.getProperty(GlobalConfig.OPERATION_LOG_SIZE_NAME, GlobalConfig.OPERATION_LOG_SIZE_DEFAULT);
    private MmapFile mmapFile;
    private String operationLogFile;

    public MmapOperationFile(RecoveryService recoveryService) throws IOException {
        super(recoveryService);
        try {
            CheckPointFile.CheckPoint checkPoint = recoveryService.getCheckPoint();
            if (checkPoint == null || checkPoint.getFileName() == null) {
                throw new NoCheckPointException("Checkpoint file miss.");
            }
            operationLogFile = recoveryService.getOperationLogPath() + "/" + checkPoint.getFileName();
            if (!FileUtils.exists(operationLogFile)) {
                FileUtils.createFileRecursion(operationLogFile);
            }
            mmapFile = new MmapFile(operationLogFile, operationLogSize, checkPoint.getPosition());
        } catch (IOException e) {
            log.error("", e);
            throw e;
        }
    }

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

    @Override
    Iterable<OperationLog> loadOperationLogs(final int position) {

        return new Iterable<OperationLog>() {
            @Override
            public Iterator<OperationLog> iterator() {
                return new Iterator<OperationLog>() {
                    int currentPosition = position;
                    @Override
                    public boolean hasNext() {


                        return false;
                    }

                    @Override
                    public OperationLog next() {
                        int size = mmapFile.readInt(currentPosition);
                        currentPosition += 4;
                        int operationCode = mmapFile.readInt(currentPosition);
                        Operation operation = Operation.getOperation(operationCode);
                        currentPosition += 4;
                        long timestamp = mmapFile.readLong(currentPosition);
                        currentPosition += 4;
                        byte[] bytes = mmapFile.readBytes(currentPosition, size);
                        currentPosition += size;
                        return new OperationLog(size, operation, timestamp, bytes);
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        };
    }

    private void appendLog(OperationLog operationLog) {
        mmapFile.appendInt(operationLog.getSize());
        mmapFile.appendInt(operationLog.getOperation().getCode());
        mmapFile.appendLong(operationLog.getTimestamp());
        mmapFile.appendBytes(operationLog.getData());
    }
}
