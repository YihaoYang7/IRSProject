package com.irsproject.word2vec.org.nlp.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Counter<T> {
    private HashMap<T, CountInteger> hm = null;

    public Counter() {
        hm = new HashMap<T, CountInteger>();
    }

    public Counter(int initialCapacity) {
        hm = new HashMap<T, CountInteger>(initialCapacity);
    }

    public class CountInteger{
        private int count;

        public CountInteger(int initCount){
            count = initCount;
        }

        public void set(int num){
            count = num;
        }

        public int value(){
            return count;
        }

        @Override
        public String toString(){
            return "Count: " + String.valueOf(count);
        }
    }


    public void add(T t, int n) {

        CountInteger newCount = new CountInteger(n);
        CountInteger oldCount = hm.put(t, newCount);

        if (oldCount != null){
            newCount.set(oldCount.value() + n);
        }

    }


    public void add(T t) {
        this.add(t, 1);
    }


    public int get(T t){
        CountInteger count = hm.get(t);
        if (count == null){
            return 0;
        } else {
            return count.value();
        }
    }


    public int size() {
        return hm.size();
    }


    public void remove(T t) {
        hm.remove(t);
    }


    public Set<T> keySet(){
        return hm.keySet();
    }


    @Override
    public String toString(){
        Iterator<Entry<T, CountInteger>> iterator = this.hm.entrySet().iterator() ;
        StringBuilder sb = new StringBuilder() ;
        Entry<T, CountInteger> next = null ;
        while(iterator.hasNext()){
            next = iterator.next() ;
            sb.append(next.getKey()) ;
            sb.append("\t") ;
            sb.append(next.getValue()) ;
            sb.append("\n") ;
        }
        return sb.toString() ;
    }

    public static void main(String[] args) {

        String[] strKeys = {"1", "2", "3", "1", "2", "1", "3", "3", "3", "1", "2"};
        Counter<String> counter = new Counter<String>();
        for (String strKey : strKeys){
            counter.add(strKey);
        }
        for (String strKey : counter.keySet()){
            System.out.println(strKey + " : " + counter.get(strKey));
        }
        System.out.println(counter.get("9"));
//        System.out.println(Long.MAX_VALUE);
    }
}
