package com.nyver.bbclearningenglish.exception;

public class StorageException extends MyException {
    public StorageException(Throwable throwable) {
        super(throwable);
    }

    public StorageException(String detailMessage) {
        super(detailMessage);
    }
}
