package Exercicio2;
import java.util.ArrayList;

public class ArrayListSafe<V> {
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
