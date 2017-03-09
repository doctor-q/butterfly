/*
 * Copy right here.
 */

/**
 * 负责分布式管理,包含选主,路由,分片功能
 * 使用zookeeper管理目录
 * 本地数据目录结构
 * document->node->shard        //路由过程
 * es/data/node/index/shard/field.dict          //词典
 *                         /field/invert.idx    //域对应的倒排索引
 *                         /operationlog/20170203102030.op  //操作日志
 *                         /checkpoint          //检查点
 *                         /source/20170203102030.so    //源
 */
package cc.doctor.wiki.search.server.cluster;