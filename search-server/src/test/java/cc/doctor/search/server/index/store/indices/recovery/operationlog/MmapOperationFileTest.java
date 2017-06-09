package cc.doctor.search.server.index.store.indices.recovery.operationlog;

import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.client.query.document.Document;
import cc.doctor.search.client.rpc.operation.Operation;
import cc.doctor.search.common.utils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Created by doctor on 2017/3/16.
 */
public class MmapOperationFileTest {
    @Before
    public void setUp() {
        PropertyUtils.setProperty(GlobalConfig.OPERATION_LOG_SIZE_NAME, 10 * 1024);
    }
    @Test
    public void appendOperationLogInner() throws Exception {
        CheckPointFile checkPointFile = new CheckPointFile();
        OperationLogFile operationLogFile = new MmapOperationFile(checkPointFile);
        for (int i = 0; i < 1000; i++) {
            OperationLog operationLog = new OperationLog();
            operationLog.setOperation(Operation.ADD_DOCUMENT);
            operationLog.setTimestamp(new Date().getTime());
            operationLog.setData(new Document());
            operationLogFile.appendOperationLog(operationLog);
        }
    }

    @Test
    public void loadOperationLogs() throws Exception {
        CheckPointFile checkPointFile = new CheckPointFile();
        OperationLogFile operationLogFile = new MmapOperationFile(checkPointFile);
        Iterable<OperationLog> operationLogs = operationLogFile.loadOperationLogs(0);
        for (OperationLog operationLog : operationLogs) {
            System.out.println(operationLog);
        }
    }

}