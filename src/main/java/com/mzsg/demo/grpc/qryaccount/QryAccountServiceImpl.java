package com.mzsg.demo.grpc.qryaccount;

import com.mzsg.demo.grpc.qryaccount.QryAccountProto;
import com.mzsg.demo.grpc.qryaccount.QryAccountProto.AccountQryRequest;
import com.mzsg.demo.grpc.qryaccount.QryAccountProto.AccountQryResponse;

import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;

public class QryAccountServiceImpl extends QryAccountServiceGrpc.QryAccountServiceImplBase {
    /**
     * <pre>
     * 账户查询方法
     * </pre>
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void qry(AccountQryRequest request, StreamObserver<AccountQryResponse> responseObserver) {
        System.out.println("qry " + request.getUserId());
        AccountQryResponse response = QryAccountProto.AccountQryResponse.newBuilder().setRc(1).setAmount(666).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
