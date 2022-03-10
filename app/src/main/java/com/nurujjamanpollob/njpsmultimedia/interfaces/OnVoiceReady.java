package com.nurujjamanpollob.njpsmultimedia.interfaces;

public interface OnVoiceReady {

    default void onVoiceToTextResult(String result){}
    default void onTextBufferReceived(String textBuffer){}
}