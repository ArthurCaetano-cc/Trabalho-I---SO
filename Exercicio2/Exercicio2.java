package Exercicio2;
import java.util.ArrayList;
import java.util.Random;

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

    public synchronized int find(V value){
        for(int i = 0; i<this.arr.size(); i++){
            if(this.arr.get(i) == value){
                return i;
            }
        }

        return -1;
    }

    public synchronized void remove(int index){
        arr.remove(index);
    }

    public synchronized V pop(int index){
        V value = arr.get(index);
        arr.remove(index);
        return value;
    }

    public synchronized boolean contains(V value) {
        return arr.contains(value);
    }
}

class ThreadTests extends Thread {
    private int rank;
    private ArrayListSafe<Integer> arr;

    public ThreadTests(int rank, ArrayListSafe<Integer> arr){
        this.rank = rank;
        this.arr = arr;
    }

    @Override 
    public void run(){
        Random random = new Random();
        for(int i = 0; i < 5; i++){
            int randomNumber = random.nextInt(100); // valores entre 0 e 99
            arr.insertInto(randomNumber);
            System.out.printf("Thread %d inseriu o valor %d no array\n", this.rank, randomNumber);
        }
    }
}

public class Exercicio2 {
    public static void main(String[] args) throws InterruptedException {
        ArrayListSafe<Integer> arr = new ArrayListSafe<>();
        ThreadTests[] threads = new ThreadTests[5];

        for(int i = 0; i < 5; i++){
            threads[i] = new ThreadTests(i, arr);
            threads[i].start();
        }

        for (int i = 0; i < 5; i++) {
            threads[i].join();
        }
        
        int size = arr.getSize();

        System.out.println("Tamanho final do array: " + size);
        System.out.println("Conteúdo final do Array:");
        for(int i = 0; i < size; i++){
            System.out.printf("%d ", arr.get(i));
        }
        System.out.println();
    }
}
