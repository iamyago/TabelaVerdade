package Logic;

import java.util.LinkedList;
import java.util.Queue;

public class RecentCounter {
    Queue<Integer> requests;
    // Queue é uma interface que representa uma fila.
    public RecentCounter() {
        // linkedinlist percorre a fila que foi criado com Queue da forma mais rapida possivel.
        // instaciar a fila.
        requests = new LinkedList<Integer>();
    }
   public int ping(int t) {
        requests.add(t);
        int range = t - 3000;
        while (requests.peek() < range){
            requests.poll();
        }
        return requests.size();
    }

}
