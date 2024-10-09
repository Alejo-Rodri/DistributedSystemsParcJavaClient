package com.grpc.util;

import io.grpc.stub.StreamObserver;

public class StreamsManager<T> {

    public StreamObserver<T> getStreamObserver(String onNextMessage, String OnErrorMessage, String onCompleteMessage) {
        StreamObserver<T> responseObserver = new StreamObserver<T>() {
            @Override
            public void onNext(T response) {
                System.out.println(onNextMessage);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println(OnErrorMessage + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println(onCompleteMessage);
            }
        };
        return responseObserver;
    }
}
