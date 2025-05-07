package com.nexuscale.nexusscalemanage.jvm;

import java.util.*;

public class Test {
    public int numberOfCuts(int n) {
        if (n % 2 == 0){  // 偶数个
            return n / 2;
        }else{
            // 奇数个  3--> 3     5
            return n;
        }

    }
    public static boolean isValid(String s) {
        int n = s.length();
        if (n % 2 == 1) {
            return false;
        }

        Map<Character, Character> pairs = new HashMap<Character, Character>() {{
            put(')', '(');
            put(']', '[');
            put('}', '{');
        }};
        Deque<Character> stack = new LinkedList<Character>();
        for (int i = 0; i < n; i++) {
            char ch = s.charAt(i);
            if (pairs.containsKey(ch)) {
                if (stack.isEmpty() || stack.peek() != pairs.get(ch)) {
                    return false;
                }
                stack.pop();
            } else {
                stack.push(ch);
            }
        }
        return stack.isEmpty();
    }
    public static void main(String[] args) {
        isValid("()");
//        Base64.Decoder decoder = Base64.getDecoder();
//        System.out.println(new String(decoder.decode("5oGt5Zac5om+5Yiw56ys5LiA5q2l77yB6K+36K6h566X5Lul5LiL5L2N6L+Q566X6KGo6L6+5byP55qE57uT5p6c77yaCgooKCgoKCgoKCgoKDB4QTdCM0M5IDw8IDMpIHwgKDB4RDJFNUYxID4+IDIpKSAmIDB4RkZGRkZGKSBeIDB4NDI0MDMpICsgMHgxMzM3KSAmIDB4RkZGRkZGKSAqIDB4MTkpIF4gKCh+KCgweEJFRUYgPDwgNCkgJiAweEYwRjBGMCkpICYgMHhGRkZGRkYpKSA+PiAzKSAmIDB4N0ZGRkYpIF4gKCgoMHhDMEZGRUUgJiAweEYwMDAwRikgPDwgMikgfCAweEExRTAzKSkgJiAweEZGRkZGCgrkvYbmmK/ov5jmsqHnu5PmnZ/vvIzov5jor7fov5vkuIDmraXmjqLntKLpobXpnaI=")));

        // 混淆的艺术
//    const _ = [
//        'Y3JlYXRlUmVxdWVzdA==',
//                'L2FwaS9hY3Rpb24=',
//                'Y29kZT3kvaDlvpfliLDnmoRjb2RlKEhFWOWwj+WGme+8jOS4jeW4pjB45YmN57yAKQ=='
//    ].map(a => atob(a));
//
//        function _0x3a8f() {
//            return {
//                    url: _[1],
//                    params: _[2]
//      };
//        }

    }
}
