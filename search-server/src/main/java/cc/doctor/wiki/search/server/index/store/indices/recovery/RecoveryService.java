package cc.doctor.wiki.search.server.index.store.indices.recovery;

import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.client.query.QueryBuilder;
import cc.doctor.wiki.search.client.query.document.Document;
import cc.doctor.wiki.search.client.rpc.operation.Operation;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.CheckPointFile;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.MmapOperationFile;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.OperationLog;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.OperationLogFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;
import static cc.doctor.wiki.search.server.index.manager.IndexManagerContainer.indexManagerContainer;

/**
 * Created by doctor on 2017/3/9.
 * 数据恢复服务,负责操作日志的管理和索引的恢复,每个node一个
 */
public class RecoveryService {
    private OperationLogFile operationLogFile;  //操作日志目录
    private CheckPointFile checkPointFile;
    public static final boolean OPERATION_LOG_QUERY = settings.getBoolean(GlobalConfig.OPERATION_LOG_QUERY);

    public RecoveryService() {
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
                indexManagerContainer.createIndex(schema);
                break;
            case DROP_INDEX:
                schema = (Schema) operationLog.getData();
                indexManagerContainer.dropIndex(schema);
                break;
            case PUT_SCHEMA:
                schema = (Schema) operationLog.getData();
                indexManagerContainer.putSchema(schema);
                break;
            case PUT_ALIAS:
                Tuple<String, String> alias = (Tuple<String, String>) operationLog.getData();
                indexManagerContainer.putAlias(alias);
                break;
            case DROP_ALIAS:
                alias = (Tuple<String, String>) operationLog.getData();
                indexManagerContainer.dropAlias(alias);
                break;
            case ADD_DOCUMENT:
                Tuple<String, Document> documentTuple = (Tuple<String, Document>) operationLog.getData();
                indexManagerContainer.insertDocument(documentTuple.getT1(), documentTuple.getT2());
                break;
            case BULK_INSERT:
                Tuple<String, List<Document>> documentsTuple = (Tuple<String, List<Document>>) operationLog.getData();
                indexManagerContainer.bulkInsert(documentsTuple.getT1(), documentsTuple.getT2());
                break;
            case DELETE_DOCUMENT:
                Tuple<String, Long> docIdTuple = (Tuple<String, Long>) operationLog.getData();
                indexManagerContainer.deleteDocument(docIdTuple.getT1(), docIdTuple.getT2());
                break;
            case BULK_DELETE:
                Tuple<String, List<Long>> docIdsTuple = (Tuple<String, List<Long>>) operationLog.getData();
                indexManagerContainer.bulkDelete(docIdsTuple.getT1(), docIdsTuple.getT2());
                break;
            case DELETE_BY_QUERY:
                Tuple<String, QueryBuilder> queryBuilderTuple = (Tuple<String, QueryBuilder>) operationLog.getData();
                indexManagerContainer.deleteByQuery(queryBuilderTuple.getT1(), queryBuilderTuple.getT2());
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
}
