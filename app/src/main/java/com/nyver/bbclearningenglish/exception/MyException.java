package com.nyver.bbclearningenglish.exception;

public class MyException extends Exception {
    public MyException(Throwable throwable) {
        super(throwable);
    }

    public MyException(String detailMessage) {
        super(detailMessage);
    }
}
