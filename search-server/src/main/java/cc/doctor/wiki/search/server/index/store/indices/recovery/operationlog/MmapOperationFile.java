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
        scrollFile = new MmapScrollFile(operationRoot, operationLogSize, new ScrollFile.ScrollFileNameStrategy() {

            @Override
            public String first() {
                return "1";
            }

            @Override
            public String next(String current) {
                return String.valueOf(Integer.parseInt(current) + 1);
            }
        }, checkPoint);
    }

    @Override
    public boolean appendOperationLogInner(OperationLog operationLog) {
        scrollFile.writeSerializable(operationLog);
        return true;
    }

    //load operation logs lazy
    @Override
    Iterable<OperationLog> loadOperationLogs(final long position) {
        return new Iterable<OperationLog>() {
            @Override
            public Iterator<OperationLog> iterator() {
                return new Iterator<OperationLog>() {
                    long pos = position;

                    @Override
                    public boolean hasNext() {
                        return pos >= 0;
                    }

                    @Override
                    public OperationLog next() {
                        Tuple<Long, OperationLog> operationLogTuple = scrollFile.readSerializable(pos);
                        pos = operationLogTuple.getT1();
                        return operationLogTuple.getT2();
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        };
    }
}
