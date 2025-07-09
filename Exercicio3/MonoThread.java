package Exercicio3;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import Exercicio2.ArrayListSafe;

class FourValues<F, S, T, Fth> {
    public final F first;
    public final S second;
    public final T third;
    public final Fth fourth;

    public FourValues(F first, S second, T third, Fth fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public F getFirst() { return first; }
    public S getSecond() { return second; }
    public T getThird() { return third; }
    public Fth getFourth() { return fourth; }
}

class Helper {
    private final ArrayListSafe<Integer> arrSafe = new ArrayListSafe<>();
    private final ArrayList<Integer> arrOriginal = new ArrayList<>();
    private final int size;
    private final String sizeStr;
    private final DecimalFormat formato;

    public Helper(int size, String sizeStr) {
        this.size = size;
        this.sizeStr = sizeStr;
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
        simbolos.setDecimalSeparator(',');
        simbolos.setGroupingSeparator('.');
        this.formato = new DecimalFormat("#,##0.000", simbolos);
    }

    private FourValues<Double, Double, Double, Double> medirTempo(Runnable safeOp, Runnable originalOp) {
        long begin = System.nanoTime();
        safeOp.run();
        long safeEnd = System.nanoTime();
        originalOp.run();
        long originalEnd = System.nanoTime();

        double safeTimeMs = (safeEnd - begin) / 1_000_000.0;
        double originalTimeMs = (originalEnd - safeEnd) / 1_000_000.0;

        return new FourValues<>(
            safeTimeMs,
            originalTimeMs,
            operationsPerSecond(safeTimeMs),
            operationsPerSecond(originalTimeMs)
        );
    }

    private double operationsPerSecond(double timeMs) {
        return (1000.0 * size) / timeMs;
    }

    public FourValues<Double, Double, Double, Double> insertions() {
        return medirTempo(() -> {
            for (int i = 0; i < size; i++) arrSafe.insertInto(i);
        }, () -> {
            for (int i = 0; i < size; i++) arrOriginal.add(i);
        });
    }

    public FourValues<Double, Double, Double, Double> searchs() {
        return medirTempo(() -> arrSafe.find(size + 15), () -> {
            for (int i = 0; i < size; i++) {
                if ((size + 15) == arrOriginal.get(i)) break;
            }
        });
    }

    public FourValues<Double, Double, Double, Double> removals() {
        return medirTempo(() -> {
            for (int i = size - 1; i >= 0; i--) arrSafe.remove(i);
        }, () -> {
            for (int i = size - 1; i >= 0; i--) arrOriginal.remove(i);
        });
    }

    private String format(double value) {
        return formato.format(value);
    }

    public void run() {
        var insertions = insertions();
        var searchs = searchs();
        var removals = removals();

        System.out.printf("\n-----Tamanho das listas: %,d (%s)-----\n", size, sizeStr);
        System.out.printf("%-10s | %-22s | %-22s\n", "Operação", "ArrayList (ms)", "ArrayListSafe (ms)");
        printLine("Inserção", insertions.getSecond(), insertions.getFirst());
        printLine("Busca", searchs.getSecond(), searchs.getFirst());
        printLine("Remoção", removals.getSecond(), removals.getFirst());

        System.out.println("\nOperações por segundo:\n");
        System.out.printf("%-10s | %-22s | %-22s\n", "Operação", "ArrayList", "ArrayListSafe");
        printLine("Inserção", insertions.getFourth(), insertions.getThird());
        printLine("Busca", searchs.getFourth(), searchs.getThird());
        printLine("Remoção", removals.getFourth(), removals.getThird());
    }

    private void printLine(String label, double original, double safe) {
        System.out.printf("%-10s | %-22s | %-22s\n", label, format(original), format(safe));
    }
}

public class MonoThread {
    public static void main(String[] args) {
        new Helper(1000, "Mil").run();
        new Helper(100_000, "Cem Mil").run();
        new Helper(10_000_000, "Dez Milhões").run();
    }
}
