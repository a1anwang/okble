package com.a1anwang.okble.common;

import java.util.LinkedList;

/**
 * Created by a1anwang.com on 2018/5/16.
 */

public class BLEOperationQueue<T> {
    private LinkedList<T> bleOperations=new LinkedList<>();


    synchronized public void add(T operation){

        bleOperations.add(operation);
    }

    synchronized public T removeFirst(){
       return  bleOperations.removeFirst();
    }

    public void  clear(){
        bleOperations.clear();
    }

    public int getOperationSize(){
        return  bleOperations.size();
    }

    public T get(int index){
        return  bleOperations.get(index);
    }

}
