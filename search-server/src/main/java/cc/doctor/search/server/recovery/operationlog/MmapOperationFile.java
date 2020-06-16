package cc.doctor.search.server.recovery.operationlog;

import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.common.exceptions.index.NoCheckPointException;
import cc.doctor.search.common.utils.PropertyUtils;
import cc.doctor.search.store.StoreConfigs;
import cc.doctor.search.store.mm.MmapScrollFile;
import cc.doctor.search.store.mm.ScrollFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

import static cc.doctor.search.store.mm.ScrollFile.AutoIncrementScrollFileNameStrategy.autoIncrementScrollFileNameStrategy;

/**
 * Created by doctor on 2017/3/8.
 */
public class MmapOperationFile extends OperationLogFile {
    private static final Logger log = LoggerFactory.getLogger(OperationLogFile.class);
    private static final int operationLogSize = PropertyUtils.getProperty(StoreConfigs.OPERATION_LOG_SIZE_NAME, StoreConfigs.OPERATION_LOG_SIZE_DEFAULT);
    private ScrollFile scrollFile;
    private CheckPointFile checkPointFile;

    public MmapOperationFile(CheckPointFile checkPointFile) throws IOException {
        Long checkPoint = checkPointFile.getCheckPoint();
        if (checkPoint == null) {
            throw new NoCheckPointException("Checkpoint file miss.");
        }
        scrollFile = new MmapScrollFile(operationRoot, operationLogSize, autoIncrementScrollFileNameStrategy, checkPoint);
    }

    @Override
    public boolean appendOperationLogInner(OperationLog operationLog) {
        scrollFile.writeSerializable(operationLog);
        return true;
    }

    //load operation logs lazy
    @Override
    public Iterable<OperationLog> loadOperationLogs(final long position) {
        return new Iterable<OperationLog>() {
            @Override
            public Iterator<OperationLog> iterator() {
                return new Iterator<OperationLog>() {
                    long pos = position;
                    OperationLog operationLog;

                    @Override
                    public boolean hasNext() {
                        Tuple<Long, OperationLog> operationLogTuple = scrollFile.readSerializable(pos);
                        if (operationLogTuple == null) {
                            return false;
                        } else {
                            pos = operationLogTuple.getT1();
                            operationLog = operationLogTuple.getT2();
                            return true;
                        }
                    }

                    @Override
                    public OperationLog next() {
                        return operationLog;
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        };
    }
}
