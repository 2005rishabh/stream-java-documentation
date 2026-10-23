import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.HashMap;

public class CollectionsExample {
    public static void main(String[] args) {
        Set<Integer> uniqueInOrder =
                new LinkedHashSet<>(Arrays.asList(3, 1, 3, 2, 1));

        Map<Integer, Integer> frequency = new HashMap<>();
        for (int value : Arrays.asList(3, 1, 3, 2, 1)) {
            frequency.merge(value, 1, Integer::sum);
        }

        Queue<String> queue = new ArrayDeque<>();
        queue.offer("first");
        queue.offer("second");

        System.out.println("Unique in insertion order: " + uniqueInOrder);
        System.out.println("Frequencies: " + frequency);
        System.out.println("Queue head: " + queue.peek());
        System.out.println("Dequeued: " + queue.poll());
    }
}
