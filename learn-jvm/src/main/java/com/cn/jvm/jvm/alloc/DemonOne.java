package com.cn.jvm.jvm.alloc;

import org.locationtech.jts.util.CollectionUtil;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

import java.util.*;
import java.util.concurrent.locks.LockSupport;

/**
 * @Description: DemonOne
 * @Author: 一方通行
 * @Date: 2022-05-11
 * @Version:v1.0
 */
public class DemonOne {
    public static void main(String[] args) {

    /*    char o = '2';
        System.out.println("maureen test:" + o);
        System.out.println(ClassLayout.parseInstance(o).toPrintable());
        HashMap hashMap = new HashMap();
        hashMap.put("123123", "213123");
        System.out.println(GraphLayout.parseInstance(hashMap).toPrintable());
        LockSupport.park();
*/

        Set<String> notCanUseStandardIds = new HashSet<String>();



        if(!notCanUseStandardIds.contains("117153")){
           System.out.println("true ...");
        }


    }


}
