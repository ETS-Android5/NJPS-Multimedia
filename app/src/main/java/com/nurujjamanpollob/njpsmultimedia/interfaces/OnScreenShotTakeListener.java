package com.nurujjamanpollob.njpsmultimedia.interfaces;

public interface OnScreenShotTakeListener{

    default void onBitmapCaptureError(String message){}
    default void onBitmapCaptureSuccess(){}
}
