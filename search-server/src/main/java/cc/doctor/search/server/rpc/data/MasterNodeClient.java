package cc.doctor.search.server.rpc.data;

import cc.doctor.search.client.rpc.result.PingResult;

/**
 * data node communicate with master
 */
public interface MasterNodeClient {
    PingResult ping(PingMasterRequest pingMasterRequest);
}
