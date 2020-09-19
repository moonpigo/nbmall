package com.nbmall.newbeemall.common;

public class NewBeeMallException extends RuntimeException {
    public NewBeeMallException() {

    }

    public NewBeeMallException(String message) {
        super(message);
    }

    public static void fail(String message){
        new NewBeeMallException(message);
    }
}
