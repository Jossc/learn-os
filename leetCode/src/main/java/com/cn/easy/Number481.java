package com.cn.easy;

/**
 * @Description: Number481
 * @Author: 一方通行
 * @Date: 2022-10-31
 * @Version:v1.0
 */
public class Number481 {

    public int magicalString(int n) {
        int arr[]=new int[n+10];
        arr[0]=1;
        arr[1]=arr[2]=2;
        int end=2,p1=1,p2=3;
        while(p2<n){
            p1++;
            end=3-end;
            for(int i=0;i<arr[p1];i++){
                arr[p2]=end;
                p2++;
            }
        }
        int ans=0;
        for(int i=0;i<n;i++){if(arr[i]==1){ans++;}}
        return ans;
    }
}
