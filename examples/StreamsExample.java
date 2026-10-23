import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamsExample {
    public static void main(String[] args) {
        List<String> words = List.of("java", "stream", "map", "java");

        List<String> longUppercaseWords = words.stream()
                .distinct()
                .filter(word -> word.length() >= 4)
                .map(String::toUpperCase)
                .sorted()
                .toList();

        Map<Integer, Long> countByLength = words.stream()
                .collect(Collectors.groupingBy(
                        String::length,
                        Collectors.counting()));

        List<Integer> flattened = List.of(
                        List.of(1, 2),
                        List.of(3, 4))
                .stream()
                .flatMap(List::stream)
                .toList();

        System.out.println("Long distinct words: " + longUppercaseWords);
        System.out.println("Word counts by length: " + countByLength);
        System.out.println("Flattened values: " + flattened);
    }
}
