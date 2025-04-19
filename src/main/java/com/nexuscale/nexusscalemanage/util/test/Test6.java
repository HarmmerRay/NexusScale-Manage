package com.nexuscale.nexusscalemanage.util.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Test6 {
    public static void main(String[] args) {
        Set<Map.Entry<Character, Integer>> set = new HashSet<>();
        HashMap<Character, Integer> map = new HashMap<>();
        map.put('b',3);
        map.put('a',1);
        map.put('a',map.get('a')+1);
        System.out.println(map.get('a'));
        System.out.println(map.containsKey('a'));
        map.keySet().forEach(System.out::println);
        map.forEach((k,v)->{
            System.out.println(v);
        });
    }
}
