package com.a1anwang.okble_demo.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by a1anwang.com on 2018/5/22.
 */

public class MyLinkedHashMap<K,V>{

    private List<V> list;
    private HashMap<K,Integer> hashMap;

    public MyLinkedHashMap() {
        super();
        list=new ArrayList<>();
        hashMap=new HashMap<>();
    }

    /**
     *
     * @param key
     * @param value
     * @return  int[] 2个长度的数组,第一个数字代表 value对应的index, 第二个数字为1代表这是新增数据(key是新的),第二个数字为0代表不是新数据(key是旧的)
     */
    public int[]  put(K key, V value){
        int index=0;

        if(!hashMap.containsKey(key)){
            list.add(value);
            index=list.size()-1;
            hashMap.put(key,Integer.valueOf(index));

            return new int[]{index,1};
        }else{
            int oldIndex=hashMap.get(key);
            list.set(oldIndex,value);
            index=oldIndex;
            return new int[]{index,0};
        }

    }

    public V get(int index){
        return list.get(index);
    }

    public int size(){
        return list.size();
    }

    public void clear(){
        list.clear();
        hashMap.clear();
    }



}
