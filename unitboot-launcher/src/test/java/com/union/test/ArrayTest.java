package com.union.test;

import org.junit.Assert;
import org.junit.Test;

/**
 * 数组测试
 */
public class ArrayTest {

    @Test
    public void testRefrence(){
        final A a = new A();
        final String[] entries = a.getEntries();
        for(int i=0;i<entries.length;i++){
            entries[i] = null;
        }
        Assert.assertNull(entries[0]);
    }
}

class A{

    private String[] entries ;
    public A(){
        entries = new String[]{"1","2","3"};
    }
    public String[] getEntries(){
        return entries;
    }
}
