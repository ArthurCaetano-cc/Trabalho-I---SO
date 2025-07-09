package Exercicio1;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class BarbeiroDorminhoco {
    private static final int MAX_CADEIRAS_ESPERA = 10;
    private static final int NUM_BARBEIROS = 2;

    // Semáforos para controle de sincronização
    private final Semaphore clientesEsperando = new Semaphore(0);
    private final Semaphore barbeirosDisponiveis = new Semaphore(NUM_BARBEIROS);
    private final Semaphore mutex = new Semaphore(1);

    // Contador de clientes esperando
    private final AtomicInteger clientesNaFila = new AtomicInteger(0);

    // Contador de clientes atendidos
    private final AtomicInteger clientesAtendidos = new AtomicInteger(0);

    // Gerador de números aleatórios
    private final Random random = new Random();

    public static void main(String[] args) {
        BarbeiroDorminhoco barbearia = new BarbeiroDorminhoco();
        barbearia.iniciar();
    }

    public void iniciar() {
        System.out.println("Barbearia aberta com " + NUM_BARBEIROS + " barbeiros!");
        System.out.println("Capacidade máxima da fila: " + MAX_CADEIRAS_ESPERA + " clientes");
        System.out.println("" + "=".repeat(50));

        // Cria e inicia as threads dos barbeiros
        for (int i = 1; i <= NUM_BARBEIROS; i++) {
            Thread barbeiro = new Thread(new Barbeiro(i));
            barbeiro.start();
        }

        // Cria e inicia a thread do gerador de clientes
        Thread geradorClientes = new Thread(this::gerarClientes);
        geradorClientes.start();

        // Mantem o programa rodando
        try {
            geradorClientes.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void gerarClientes() {
        int numeroCliente = 1;

        while (true) {
            try {
                // Espera um tempo aleatório entre 4s e 6s para novo cliente
                int tempoEspera = 4000 + random.nextInt(2001); // 4000-6000ms
                Thread.sleep(tempoEspera);

                // Cria um novo cliente
                Thread cliente = new Thread(new Cliente(numeroCliente++));
                cliente.start();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private class Barbeiro implements Runnable {
        private final int id;

        public Barbeiro(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // Espera por cliente
                    System.out.println("Barbeiro " + id + " está dormindo...");
                    clientesEsperando.acquire(); // Bloqueia até haver cliente

                    // Acorda e prepara para cortar
                    mutex.acquire();
                    int clientesRestantes = clientesNaFila.decrementAndGet();
                    System.out.println("✂Barbeiro " + id + " acordou! Clientes na fila: " + clientesRestantes);
                    mutex.release();

                    // Corta o cabelo (tempo aleatório entre 5s e 10s)
                    int tempoCorte = 5000 + random.nextInt(5001); // 5000-10000ms
                    System.out.println("Barbeiro " + id + " cortando cabelo... (" + (tempoCorte/1000) + "s)");
                    Thread.sleep(tempoCorte);

                    // Finaliza o corte
                    int totalAtendidos = clientesAtendidos.incrementAndGet();
                    System.out.println("Barbeiro " + id + " terminou o corte! Total atendidos: " + totalAtendidos);

                    // Libera o barbeiro
                    barbeirosDisponiveis.release();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private class Cliente implements Runnable {
        private final int id;

        public Cliente(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("Cliente " + id + " chegou na barbearia");

                // Tenta entrar na fila
                mutex.acquire();

                if (clientesNaFila.get() < MAX_CADEIRAS_ESPERA) {
                    // Há espaço na fila
                    int posicaoFila = clientesNaFila.incrementAndGet();
                    System.out.println("Cliente " + id + " entrou na fila (posição " + posicaoFila + "/" + MAX_CADEIRAS_ESPERA + ")");

                    // Sinaliza que há um cliente esperando
                    clientesEsperando.release();
                    mutex.release();

                    // Espera o barbeiro ficar disponível
                    barbeirosDisponiveis.acquire();
                    System.out.println("Cliente " + id + " sentou na cadeira do barbeiro");

                } else {
                    // Fila está cheia
                    System.out.println("Cliente " + id + " foi embora - fila está cheia! (" + MAX_CADEIRAS_ESPERA + "/" + MAX_CADEIRAS_ESPERA + ")");
                    mutex.release();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

// Ass: Carlos Alberto dos Santos Neto.