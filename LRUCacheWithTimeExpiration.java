import java.util.*;
import java.time.*;

public class LRUCacheWithTimeExpiration<K, V> implements Cache<K, V>{
    private class KVNode {
        KVNode next; // points to the next node towards the head
        KVNode prev; // points to the next node towards the tail

        private Instant lastAccessedTime; // the last time this node was accessed.

        K key;
        V value;

        KVNode(K key, V value) {
            prev = null;
            next = null;

            lastAccessedTime = Instant.now();

            this.key = key;
            this.value = value;
        }

        /**
         * @return true iff the node's last-accessed time was longer ago than the current time minus the lifetime.
         */
        boolean isExpired() {
            return Duration.between(lastAccessedTime, Instant.now()).compareTo(lifetime) > 0;
        }
    }
    private class KVLinkedList {
        KVNode head; // contains the oldest node
        KVNode tail; // contains the newest node

        int size; // number of nodes in the list

        KVLinkedList() {
            head = null;
            tail = null;
            size = 0;
        }

        /**
         * Adds a node to the tail of the list
         * @param node: node to add to the tail
         */
        void enqueue(KVNode node) {
            // if there is nothing in the list, then make node both the head and tail
            if (tail == null) {
                head = node;
                tail = node;

                node.prev = null;
                node.next = null;


                size++;
                return;
            }
            // otherwise, make the tail point to node, and make node the new tail.
            KVNode tmp = tail;
            tmp.prev = node;
            tail = node;

            node.next = tail;
            node.prev = null;

            size++;
        }

        /**
         * Removes a node from the head of the list
         * @return the removed node
         */
        KVNode dequeue() {
            KVNode node = head;
            head = node.prev;
            if (node.prev != null)
                node.prev.next = null;

            if (size == 1) // if there are no longer any nodes
                tail = null;
            size--;
            return node;
        }


        /**
         * Removes the given node from the list
         * @param node: node to remove
         * @return the node passed as input
         */
        KVNode removeNode (KVNode node) {

            if (size == 1) { // there is only one node, which must be this node
                node = head; // safer in case of bugs elsewhere
                head = null;
                tail = null;
            }
            else if (node == head) { // if this is the head: dequeue then enqueue to move it to the tail
                enqueue(dequeue());
            }
            else if (node == tail) { // if this is the tail: just set the next node to be the tail
                node.next.prev = null;
                tail = node.next;
            }
            else { // if this node is somewhere in the middle of the list
                node.next.prev = node.prev;
                node.prev.next = node.next;
            }


            // remove this node's references
            node.prev = null;
            node.next = null;
            size--;
            return node;
        }


        int size() {
            return size;
        }
    }



    private final Duration lifetime;
    private final Map<K, KVNode> data;
    private final KVLinkedList queue;

    /**
     * Creates a new LRU Cache. Mappings are removed when they are older than the current time minus the lifetime and
     * a new mapping is added to the cache.
     * @param lifetime: the duration after which nodes may expire.
     */
    public LRUCacheWithTimeExpiration(Duration lifetime) {
        this.lifetime = lifetime;
        this.data = new HashMap<>();
        this.queue = new KVLinkedList();
    }

    /**
     * Returns an Optional containing the value if a mapping exists for the key, or an empty Optional if no mapping
     * exists.
     *
     * Moves the key to the tail of the queue as it is now the most recently accessed key, and resets its
     * last-accessed time.
     *
     * Runs in O(1).
     *
     * @return Optional containing the value if a mapping exists for the key, or an empty Optional if no mapping
     *      * exists.
     */
    @Override
    public Optional<V> get(K key) {
        KVNode node = data.get(key);
        if (node == null) // if key doesn't have a mapping
            return Optional.empty();

        // move the node to the tail of the queue, as it is now the newest-accessed node
        queue.removeNode(node);
        queue.enqueue(node);

        // reset the node's last-accessed time
        node.lastAccessedTime = Instant.now();

        return Optional.of(node.value);
    }


    /**
     * Adds a key-value mapping to the cache.
     * Removes any expired mappings from the cache.
     *
     * Runs in O(n), where n is the number of expired nodes in the queue, but in the case where no keys have to be
     * evicted it runs in O(1).
     */
    @Override
    public void put(K key, V value) {
        KVNode newNode = new KVNode(key, value);
        data.put(key, newNode);
        queue.enqueue(newNode);


        while(queue.head != null && queue.tail.isExpired()) { // while there are expired nodes in the queue
            // dequeue the expired nodes and remove them from the map.
            KVNode expiredNode = queue.dequeue();
            data.remove(expiredNode.key);
        }
    }

    public static void main(String[] args) {
        // some driver code to demonstrate

        Cache<Integer, String> c = new LRUCacheWithTimeExpiration<>(Duration.ofSeconds(5));
        // lifetime is 5 seconds

        c.put(1, "a");
        c.put(2, "b");

        System.out.println(c.get(2));

        System.out.println(c.get(1));
        System.out.println(c.get(99999)); // doesn't exist, prints empty optional

        c.put(3, "c");
        c.put(4, "d");

        System.out.println(c.get(3));
        System.out.println(c.get(4));
        System.out.println(c.get(1));
        System.out.println(c.get(2));

    }
}
