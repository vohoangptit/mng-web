package com.nera.nms.utils;
import java.util.*;
interface Operator {
    boolean compare(int a, int b);
}
interface OperatorEx
{
    boolean compare(String a, String b);
}

public class OperatorUtil {

    public  static  boolean compareOps (String key, int src, int target)
    {
        Map<String, Operator> opMap = new HashMap<>();
        opMap.put(">", (int a, int b) -> (a > b));
        opMap.put("<", (int a, int b) -> (a < b));
        opMap.put("<=", (int a, int b) -> (a <= b));
        opMap.put(">=", (int a, int b) -> (a >= b));
        opMap.put("==", (int a, int b) -> (a == b));
        return opMap.get(key).compare(src, target);
    }

    public static  boolean compareStr(String key,String src, String target)
    {
        Map<String, OperatorEx> opMap = new HashMap<>();
        opMap.put("like", (String a, String b) -> b.matches(a));
        opMap.put("=", (String a, String b) -> a.equals(b));
        opMap.put("not like", (String a, String b) ->  !b.matches(a));
        return opMap.get(key).compare(src, target);
    }
    public static void main (String[] args) {
        Map<String, Operator> opMap = new HashMap<>();
        opMap.put(">", (int a, int b) -> (a > b));
        opMap.put("<", (int a, int b) -> (a < b));
        opMap.put("<=", (int a, int b) -> (a <= b));
        opMap.put(">=", (int a, int b) -> (a >= b));
        opMap.put("==", (int a, int b) -> (a == b));
    }
}
