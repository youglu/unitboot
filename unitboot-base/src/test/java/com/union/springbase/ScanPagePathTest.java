package com.union.springbase;

import org.junit.Assert;
import org.junit.Test;

public class ScanPagePathTest {
    @Test
    public void testScanPackage(){
        final String activatorClassName = "org.union.sbp.springbase.SpringBaseActivator";
        final String packagePath  = activatorClassName.substring(0,activatorClassName.lastIndexOf("."));
        Assert.assertEquals("org.union.sbp.springbase",packagePath);
    }
    @Test
    public void testStringJOin(){
        String[] strings = new String[]{"a","b","c"};
        System.out.println(String.join("*",strings));
        Assert.assertEquals("a*b*c",String.join("*",strings));
    }

}
