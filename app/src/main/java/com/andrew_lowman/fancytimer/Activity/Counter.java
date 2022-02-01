package com.andrew_lowman.fancytimer.Activity;

import android.view.View;

public class Counter {
    private int total;
    private Counter.changeListener mListener;

    public Counter(int total,Counter.changeListener listener) {
        this.total = total;
        this.mListener = listener;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setToZero(){
        this.total = 0;
    }

    public void increment(){
        this.total++;
    }

    public interface changeListener{
        void counterChange(View v);
    }
}
