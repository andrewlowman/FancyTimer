package com.andrew_lowman.fancytimer.Model;

public interface Clock {
    void start();
    void start(long timer);
    void pause();
    void cancel();
}
