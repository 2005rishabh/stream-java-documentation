import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class DsaPatternsExample {
    public static void main(String[] args) {
        int[] pair = twoSum(new int[] {2, 7, 11, 15}, 9);
        System.out.println("Two-sum indices: " + Arrays.toString(pair));

        List<List<Integer>> graph = List.of(
                List.of(1, 2),
                List.of(0, 3),
                List.of(0, 3),
                List.of(1, 2));
        System.out.println("BFS order: " + breadthFirstSearch(graph, 0));

        System.out.println("Three largest values: "
                + largestK(new int[] {4, 1, 9, 2, 7}, 3));
    }

    private static int[] twoSum(int[] values, int target) {
        Map<Integer, Integer> indexByValue = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            int needed = target - values[i];
            if (indexByValue.containsKey(needed)) {
                return new int[] {indexByValue.get(needed), i};
            }
            indexByValue.put(values[i], i);
        }
        return new int[] {-1, -1};
    }

    private static List<Integer> breadthFirstSearch(
            List<List<Integer>> graph, int start) {
        boolean[] visited = new boolean[graph.size()];
        Queue<Integer> queue = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();

        queue.offer(start);
        visited[start] = true;
        while (!queue.isEmpty()) {
            int node = queue.poll();
            order.add(node);
            for (int next : graph.get(node)) {
                if (!visited[next]) {
                    visited[next] = true;
                    queue.offer(next);
                }
            }
        }
        return order;
    }

    private static List<Integer> largestK(int[] values, int k) {
        if (k < 1 || k > values.length) {
            throw new IllegalArgumentException("k must be between 1 and the array length");
        }

        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int value : values) {
            minHeap.offer(value);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        Deque<Integer> descending = new ArrayDeque<>();
        while (!minHeap.isEmpty()) {
            descending.push(minHeap.poll());
        }
        return new ArrayList<>(descending);
    }
}
