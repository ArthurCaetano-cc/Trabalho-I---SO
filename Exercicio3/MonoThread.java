package Exercicio3;
import java.util.ArrayList;
import java.util.Random;

class Pair<K, V> {
    public final K key;
    public final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
}

class ArrayListSafe<V> {
    private final ArrayList<V> arr;

    public ArrayListSafe() {
        this.arr = new ArrayList<>();
    }

    public synchronized void insertInto(V value) {
        arr.add(value);
    }

    public synchronized int getSize() {
        return arr.size();
    }

    public synchronized V get(int index) {
        return arr.get(index);
    }

    public synchronized int find(V value) {
        for (int i = 0; i < this.arr.size(); i++) {
            if (this.arr.get(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void remove(int index) {
        arr.remove(index);
    }

    public synchronized V pop(int index) {
        V value = arr.get(index);
        arr.remove(index);
        return value;
    }

    public synchronized boolean contains(V value) {
        return arr.contains(value);
    }
}

class Helper {
    private ArrayListSafe<Integer> arrSafe;
    private ArrayList<Integer> arrOriginal;
    private int size;
    private String sizeStr;
    Random random = new Random();

    public Helper(int size, String sizeStr) {
        this.size = size;
        this.sizeStr = sizeStr;
        this.arrSafe = new ArrayListSafe<>();
        this.arrOriginal = new ArrayList<>();
    }

    public Pair<Double, Double> insertions() {
        long begin = System.nanoTime();

        for (int i = 0; i < size; i++) {
            arrSafe.insertInto(i);
        }

        long safeEnd = System.nanoTime();

        for (int i = 0; i < size; i++) {
            arrOriginal.add(i);
        }

        long originalEnd = System.nanoTime();

        double safeTimeMs = (safeEnd - begin) / 1_000_000.0;
        double originalTimeMs = (originalEnd - safeEnd) / 1_000_000.0;

        return new Pair<>(safeTimeMs, originalTimeMs);
    }

    public Pair<Double, Double> searchs() {
        long begin = System.nanoTime();
        arrSafe.find(this.size + 15);
        long safeEnd = System.nanoTime();

        for (int i = 0; i < this.size; i++) {
            if ((this.size + 15) == arrOriginal.get(i)) {
                break;
            }
        }

        long originalEnd = System.nanoTime();

        double safeTimeMs = (safeEnd - begin) / 1_000_000.0;
        double originalTimeMs = (originalEnd - safeEnd) / 1_000_000.0;

        return new Pair<>(safeTimeMs, originalTimeMs);
    }

    public Pair<Double, Double> remotions() {
        long begin = System.nanoTime();

        for (int i = size - 1; i >= 0; i--) {
            arrSafe.remove(i);
        }

        long safeEnd = System.nanoTime();

        for (int i = size - 1; i >= 0; i--) {
            arrOriginal.remove(i);
        }

        long originalEnd = System.nanoTime();

        double safeTimeMs = (safeEnd - begin) / 1_000_000.0;
        double originalTimeMs = (originalEnd - safeEnd) / 1_000_000.0;

        return new Pair<>(safeTimeMs, originalTimeMs);
    }

    public void run() {
        Pair<Double, Double> insertions = insertions();
        Pair<Double, Double> searchs = searchs();
        Pair<Double, Double> remotions = remotions();

        System.out.printf("\nTamanho das listas: %d (%s)\n", this.size, this.sizeStr);
        System.out.printf("%-10s | %-22s | %-22s\n", "Operação", "ArrayList (ms)", "ArrayListSafe (ms)");
        System.out.printf("%-10s | %-22.3f | %-22.3f\n", "Inserção", insertions.getValue(), insertions.getKey());
        System.out.printf("%-10s | %-22.3f | %-22.3f\n", "Busca", searchs.getValue(), searchs.getKey());
        System.out.printf("%-10s | %-22.3f | %-22.3f\n", "Remoção", remotions.getValue(), remotions.getKey());
    }
}

public class MonoThread {
    public static void main(String[] args) {
        Helper table1 = new Helper(1000, "Mil");
        Helper table2 = new Helper(100000, "Cem Mil");
        Helper table3 = new Helper(10000000, "Dez Milhões");

        table1.run();
        table2.run();
        table3.run();

        System.out.println("\n--------------Legenda:-----------------");
        System.out.println("Inserção - Preenchimento da estrutura com N elementos");
        System.out.println("Busca - Tentativa de buscar um elemento inexistente");
        System.out.println("Remoção - Remoção de todos os elementos");

        System.out.println("\nRelatório de Tempo:");
    }
}
