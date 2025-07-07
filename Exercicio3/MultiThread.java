package Exercicio3;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;
import Exercicio2.ArrayListSafe;

class Resultado {
    public long safeInsert, vecInsert;
    public long safeSearch, vecSearch;
    public long safeRemove, vecRemove;

    public Resultado(long si, long vi, long ss, long vs, long sr, long vr) {
        this.safeInsert = si;
        this.vecInsert = vi;
        this.safeSearch = ss;
        this.vecSearch = vs;
        this.safeRemove = sr;
        this.vecRemove = vr;
    }

    public void add(Resultado other) {
        this.safeInsert += other.safeInsert;
        this.vecInsert += other.vecInsert;
        this.safeSearch += other.safeSearch;
        this.vecSearch += other.vecSearch;
        this.safeRemove += other.safeRemove;
        this.vecRemove += other.vecRemove;
    }

    public void divide(int divisor) {
        this.safeInsert /= divisor;
        this.vecInsert /= divisor;
        this.safeSearch /= divisor;
        this.vecSearch /= divisor;
        this.safeRemove /= divisor;
        this.vecRemove /= divisor;
    }
}

class ThreadTests extends Thread {
    private final int N;
    private final ArrayListSafe<Integer> arr;
    private final Vector<Integer> vec;
    private Resultado resultado;

    public ThreadTests(int N, ArrayListSafe<Integer> arr, Vector<Integer> vec) {
        this.N = N;
        this.arr = arr;
        this.vec = vec;
    }

    public Resultado getResultado() {
        return resultado;
    }

    @Override
    public void run() {
        Random random = new Random();

        // Inserção
        long startSafeInsert = System.nanoTime();
        for (int i = 0; i < N; i++) arr.insertInto(random.nextInt(100));
        long endSafeInsert = System.nanoTime();

        long startVecInsert = System.nanoTime();
        for (int i = 0; i < N; i++) vec.add(random.nextInt(100));
        long endVecInsert = System.nanoTime();

        // Busca
        long startSafeSearch = System.nanoTime();
        arr.find(N + 1);
        long endSafeSearch = System.nanoTime();

        long startVecSearch = System.nanoTime();
        vec.contains(N + 1);
        long endVecSearch = System.nanoTime();

        // Remoção (reverso)
        long startSafeRemove = System.nanoTime();
        synchronized (arr) {
            for (int i = arr.getSize() - 1; i >= 0; i--) arr.remove(i);
        }
        long endSafeRemove = System.nanoTime();

        long startVecRemove = System.nanoTime();
        synchronized (vec) {
            for (int i = vec.size() - 1; i >= 0; i--) vec.remove(i);
        }
        long endVecRemove = System.nanoTime();

        resultado = new Resultado(
                endSafeInsert - startSafeInsert,
                endVecInsert - startVecInsert,
                endSafeSearch - startSafeSearch,
                endVecSearch - startVecSearch,
                endSafeRemove - startSafeRemove,
                endVecRemove - startVecRemove
        );
    }
}

public class MultiThread {
    private static final DecimalFormat formatter;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        formatter = new DecimalFormat("#,##0.000", symbols);
    }

    private static String ms(long nanos) {
        return formatter.format(nanos / 1_000_000.0);
    }

    private static String ops(int N, long nanos) {
        double ms = nanos / 1_000_000.0;
        double ops = (1000.0 * N) / ms;
        return formatter.format(ops);
    }

    public static void main(String[] args) throws InterruptedException {
        int numThreads = 16;
        int elementosPorThread = 1_000_000;

        ArrayListSafe<Integer> arr = new ArrayListSafe<>();
        Vector<Integer> vec = new Vector<>();
        ThreadTests[] threads = new ThreadTests[numThreads];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new ThreadTests(elementosPorThread, arr, vec);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        Resultado total = new Resultado(0, 0, 0, 0, 0, 0);
        for (ThreadTests t : threads) total.add(t.getResultado());
        total.divide(numThreads);

        System.out.println("Cada Thread irá inserir/buscar/remover " + elementosPorThread + " elementos!");

        System.out.println("\n===== MÉDIAS POR OPERAÇÃO =====");
        System.out.printf("%-10s | %-20s | %-20s\n", "Operação", "ArrayListSafe (ms / ops/s)", "Vector (ms / ops/s)");
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("%-10s | %8s ms / %10s | %8s ms / %10s\n",
                "Inserção",
                ms(total.safeInsert), ops(elementosPorThread, total.safeInsert),
                ms(total.vecInsert), ops(elementosPorThread, total.vecInsert)
        );
        System.out.printf("%-10s | %8s ms / %10s | %8s ms / %10s\n",
                "Busca",
                ms(total.safeSearch), ops(elementosPorThread, total.safeSearch),
                ms(total.vecSearch), ops(elementosPorThread, total.vecSearch)
        );
        System.out.printf("%-10s | %8s ms / %10s | %8s ms / %10s\n",
                "Remoção",
                ms(total.safeRemove), ops(elementosPorThread, total.safeRemove),
                ms(total.vecRemove), ops(elementosPorThread, total.vecRemove)
        );
    }
}
