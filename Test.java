import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;

public class Test {
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        Queue<Character> queue = new LinkedList<>();
        return 0;
    }
    public static void main(String[] args) {
        System.out.println("Hello World");
        Queue<Character> queue = new ArrayDeque<>(2);
        System.out.println(queue.add('a'));
        System.out.println(queue.add('b'));
        System.out.println(queue.add('c'));
        System.out.println(queue.poll());
        System.out.println(queue.poll());
        System.out.println(queue.poll());
    }
}