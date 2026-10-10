import java.util.concurrent.atomic.AtomicLong;

public class Mourin_Thread {

    static AtomicLong safe = new AtomicLong(0);
    static long unsafe = 0;

    static class Counter implements Runnable {
        long count = 0;
        int n;
        boolean mode;

        Counter(int n, boolean mode) {
            this.n = n;
            this.mode = mode;
        }

        public void run() {
            for (int i = 0; i < n; i++) {
                if (mode)
                    safe.incrementAndGet();
                else
                    unsafe++;

                count++;
            }
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        int t = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);
        boolean mode = Boolean.parseBoolean(args[2]);

        safe.set(0);
        unsafe = 0;

        Thread[] th = new Thread[t];
        Counter[] c = new Counter[t];

        for (int i = 0; i < t; i++) {
            c[i] = new Counter(n, mode);
            th[i] = new Thread(c[i]);
            th[i].start();
        }

        long total = 0;

        for (int i = 0; i < t; i++) {
            th[i].join();
            total += c[i].count;
        }

        long s = mode ? safe.get() : unsafe;
        long diff = Math.abs(s - total);
        double percent = total == 0
                ? (s == 0 ? 0 : Double.NaN)
                : diff * 100.0 / total;

        System.out.println("Threads: " + t);
        System.out.println("Expected: " + (long) t * n);
        System.out.println("Static: " + s);
        System.out.println("Non-static total: " + total);
        System.out.println("Absolute difference: " + diff);
        System.out.printf("Percentage difference: %.2f%%%n",
                percent);
    }
}
