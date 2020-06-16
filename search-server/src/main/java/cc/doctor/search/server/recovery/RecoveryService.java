package cc.doctor.search.server.recovery;

import cc.doctor.search.client.query.QueryBuilder;
import cc.doctor.search.client.rpc.operation.Operation;
import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.common.schema.Schema;
import cc.doctor.search.common.utils.Container;
import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.server.common.config.Settings;
import cc.doctor.search.server.index.manager.IndexManagerService;
import cc.doctor.search.server.recovery.operationlog.CheckPointFile;
import cc.doctor.search.server.recovery.operationlog.MmapOperationFile;
import cc.doctor.search.server.recovery.operationlog.OperationLog;
import cc.doctor.search.server.recovery.operationlog.OperationLogFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by doctor on 2017/3/9.
 * 数据恢复服务,负责操作日志的管理和索引的恢复,每个node一个
 */
public class RecoveryService {
    private IndexManagerService indexManagerService;
    private OperationLogFile operationLogFile;  //操作日志目录
    private CheckPointFile checkPointFile;
    public static final boolean OPERATION_LOG_QUERY = Settings.settings.getBoolean(GlobalConfig.OPERATION_LOG_QUERY);
    private Container container;

    public RecoveryService() {
        indexManagerService = container.getComponent(IndexManagerService.class);
        checkPointFile = new CheckPointFile();
        try {
            operationLogFile = new MmapOperationFile(checkPointFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        Long checkPoint = checkPointFile.getCheckPoint();
        Iterable<OperationLog> operationLogs = operationLogFile.loadOperationLogs(checkPoint);
        Iterator<OperationLog> iterator = operationLogs.iterator();
        while (iterator.hasNext()) {
            OperationLog operationLog = iterator.next();
            doOperationLog(operationLog);
        }
    }

    private void doOperationLog(OperationLog operationLog) {
        Operation operation = operationLog.getOperation();
        switch (operation) {
            case CREATE_INDEX:
                Schema schema = (Schema) operationLog.getData();
                indexManagerService.createIndex(schema);
                break;
            case DROP_INDEX:
                schema = (Schema) operationLog.getData();
                indexManagerService.dropIndex(schema);
                break;
            case PUT_SCHEMA:
                schema = (Schema) operationLog.getData();
                indexManagerService.putSchema(schema);
                break;
            case PUT_ALIAS:
                Tuple<String, String> alias = (Tuple<String, String>) operationLog.getData();
                indexManagerService.putAlias(alias);
                break;
            case DROP_ALIAS:
                alias = (Tuple<String, String>) operationLog.getData();
                indexManagerService.dropAlias(alias);
                break;
            case ADD_DOCUMENT:
                Tuple<String, Document> documentTuple = (Tuple<String, Document>) operationLog.getData();
                indexManagerService.insertDocument(documentTuple.getT1(), documentTuple.getT2());
                break;
            case BULK_INSERT:
                Tuple<String, List<Document>> documentsTuple = (Tuple<String, List<Document>>) operationLog.getData();
                indexManagerService.bulkInsert(documentsTuple.getT1(), documentsTuple.getT2());
                break;
            case DELETE_DOCUMENT:
                Tuple<String, Long> docIdTuple = (Tuple<String, Long>) operationLog.getData();
                indexManagerService.deleteDocument(docIdTuple.getT1(), docIdTuple.getT2());
                break;
            case BULK_DELETE:
                Tuple<String, List<Long>> docIdsTuple = (Tuple<String, List<Long>>) operationLog.getData();
                indexManagerService.bulkDelete(docIdsTuple.getT1(), docIdsTuple.getT2());
                break;
            case DELETE_BY_QUERY:
                Tuple<String, QueryBuilder> queryBuilderTuple = (Tuple<String, QueryBuilder>) operationLog.getData();
                indexManagerService.deleteByQuery(queryBuilderTuple.getT1(), queryBuilderTuple.getT2());
                break;
        }
    }

    public boolean createIndexOperation(Schema schema) {
        return operationLogFile.appendOperationLog(new OperationLog<>(Operation.CREATE_INDEX, schema));
    }

    public boolean dropIndexOperation(Schema schema) {
        return operationLogFile.appendOperationLog(new OperationLog<>(Operation.DROP_INDEX, schema));
    }

    public boolean putSchemaOperation(Schema schema) {
        return operationLogFile.appendOperationLog(new OperationLog<>(Operation.PUT_SCHEMA, schema));
    }

    public boolean putAliasOperation(Tuple<String, String> alias) {
        return operationLogFile.appendOperationLog(new OperationLog<>(Operation.PUT_ALIAS, alias));
    }

    public boolean dropAliasOperaion(Tuple<String, String> alias) {
        return operationLogFile.appendOperationLog(new OperationLog<>(Operation.DROP_ALIAS, alias));
    }

    public boolean insertDocumentOperation(String indexName, Document document) {
        return operationLogFile.appendOperationLog(new OperationLog<>(Operation.ADD_DOCUMENT, new Tuple<>(indexName, document)));
    }

    public boolean bulkInsertOperation(String indexName, Iterable<Document> documents) {
        return operationLogFile.appendOperationLog(new OperationLog<>(Operation.BULK_INSERT, new Tuple<>(indexName, documents)));
    }

    public boolean deleteDocumentOperation(String indexName, Long docId) {
        return operationLogFile.appendOperationLog(new OperationLog<>(Operation.DELETE_DOCUMENT, new Tuple<>(indexName, docId)));
    }

    public boolean bulkDelete(String indexName, Iterable<Long> docIds) {
        return operationLogFile.appendOperationLog(new OperationLog<>(Operation.BULK_DELETE, new Tuple<>(indexName, docIds)));
    }

    public boolean deleteByQueryOperation(String indexName, QueryBuilder queryBuilder) {
        return operationLogFile.appendOperationLog(new OperationLog<>(Operation.DELETE_BY_QUERY, new Tuple<>(indexName, queryBuilder)));
    }

    public boolean queryOperation(String indexName, QueryBuilder queryBuilder) {
        if (OPERATION_LOG_QUERY) {
            return operationLogFile.appendOperationLog(new OperationLog<>(Operation.QUERY, new Tuple<>(indexName, queryBuilder)));
        }
        return true;
    }

    public boolean flushOperation(String indexName) {
        return operationLogFile.appendOperationLog(new OperationLog<>(Operation.FLUSH, indexName));
    }
}
