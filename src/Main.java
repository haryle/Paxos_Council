import org.javatuples.Pair;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        Pair<Integer, Integer> first = Pair.with(10, 20);
        Pair<Integer, Integer> second = Pair.with(10, 10);
        System.out.println(first.compareTo(second));
    }
}