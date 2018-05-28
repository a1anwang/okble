package com.a1anwang.okble.client.core;

import java.util.LinkedList;

/**
 * Created by a1anwang.com on 2018/5/16.
 */

public class BLEOperationQueue {
    private LinkedList<BLEOperation> bleOperations;

    public BLEOperationQueue(){
        super();
        bleOperations=new LinkedList<>();
    }

    synchronized public void add(BLEOperation operation){
        bleOperations.add(operation);
    }

    synchronized public BLEOperation removeFirst(){
       return  bleOperations.removeFirst();
    }

    public void  clear(){
        bleOperations.clear();
    }

    public int getOperationSize(){
        return  bleOperations.size();
    }

    public BLEOperation get(int index){
        return  bleOperations.get(index);
    }

}
