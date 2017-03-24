package cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog;

import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.exceptions.index.NoCheckPointException;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.store.mm.MmapScrollFile;
import cc.doctor.wiki.search.server.index.store.mm.ScrollFile;
import cc.doctor.wiki.utils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

import static cc.doctor.wiki.search.server.index.store.mm.ScrollFile.AutoIncrementScrollFileNameStrategy.autoIncrementScrollFileNameStrategy;

/**
 * Created by doctor on 2017/3/8.
 */
public class MmapOperationFile extends OperationLogFile {
    private static final Logger log = LoggerFactory.getLogger(OperationLogFile.class);
    private static final int operationLogSize = PropertyUtils.getProperty(GlobalConfig.OPERATION_LOG_SIZE_NAME, GlobalConfig.OPERATION_LOG_SIZE_DEFAULT);
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
