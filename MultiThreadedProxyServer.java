import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final DoublyLinkedList<K, V> list;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.list = new DoublyLinkedList<>();
    }

    public synchronized V get(K key) {
        if (!map.containsKey(key)) {
            return null;
        }
        Node<K, V> node = map.get(key);
        list.moveToFront(node);
        return node.value;
    }

    public synchronized void put(K key, V value) {
        if (map.containsKey(key)) {
            Node<K, V> node = map.get(key);
            node.value = value;
            list.moveToFront(node);
        } else {
            if (map.size() >= capacity) {
                Node<K, V> removed = list.removeLast();
                map.remove(removed.key);
            }
            Node<K, V> newNode = new Node<>(key, value);
            list.addFirst(newNode);
            map.put(key, newNode);
        }
    }
}

class Node<K, V> {
    K key;
    V value;
    Node<K, V> prev;
    Node<K, V> next;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

class DoublyLinkedList<K, V> {
    private Node<K, V> head;
    private Node<K, V> tail;

    public DoublyLinkedList() {
        head = null;
        tail = null;
    }

    public void addFirst(Node<K, V> node) {
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }

    public void moveToFront(Node<K, V> node) {
        if (node == head) {
            return;
        }
        if (node == tail) {
            tail = tail.prev;
            tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        node.prev = null;
        node.next = head;
        head.prev = node;
        head = node;
    }

    public Node<K, V> removeLast() {
        if (tail == null) {
            return null;
        }
        Node<K, V> removed = tail;
        if (head == tail) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        return removed;
    }
}

class ProxyHandler implements Runnable {
    private final Socket clientSocket;
    private final LRUCache<String, String> cache;
    private final Semaphore semaphore;

    public ProxyHandler(Socket clientSocket, LRUCache<String, String> cache, Semaphore semaphore) {
        this.clientSocket = clientSocket;
        this.cache = cache;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream toClient = clientSocket.getOutputStream();

            // Read the HTTP request from the client
            String requestLine = fromClient.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }

            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String url = tokens[1];

            System.out.println("Handling request for URL: " + url);

            if (!method.equals("GET")) {
                sendResponse(toClient, "HTTP/1.1 501 Not Implemented\r\n\r\n");
                clientSocket.close();
                return;
            }

            // Check if the response is cached
            semaphore.acquire();
            String cachedResponse = cache.get(url);
            semaphore.release();

            if (cachedResponse != null) {
                System.out.println("Serving cached response for URL: " + url);
                sendResponse(toClient, cachedResponse);
            } else {
                try {
                    // Forward the request to the actual server
                    URL serverUrl = new URL(url);
                    HttpURLConnection serverConnection = (HttpURLConnection) serverUrl.openConnection();
                    serverConnection.setRequestMethod("GET");

                    InputStream fromServer = serverConnection.getInputStream();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fromServer.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, bytesRead);
                    }
                    buffer.flush();

                    String serverResponse = buffer.toString();
                    sendResponse(toClient, serverResponse);

                    // Cache the response
                    semaphore.acquire();
                    cache.put(url, serverResponse);
                    semaphore.release();
                } catch (FileNotFoundException e) {
                    // Handle missing resources gracefully
                    System.out.println("Resource not found for URL: " + url);
                    sendResponse(toClient, "HTTP/1.1 404 Not Found\r\n\r\nResource not found");
                }
            }

            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(OutputStream toClient, String response) throws IOException {
        toClient.write(response.getBytes());
        toClient.flush();
    }
}

public class MultiThreadedProxyServer {
    public static void main(String[] args) throws IOException {
        int port = 8080; // Proxy server port
        int cacheSize = 10; // Maximum number of cached responses
        int maxThreads = 10; // Maximum number of concurrent threads

        LRUCache<String, String> cache = new LRUCache<>(cacheSize);
        Semaphore semaphore = new Semaphore(1); // Protects the cache
        ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads); // Thread pool for handling requests

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Proxy server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + clientSocket.getInetAddress());

            threadPool.submit(new ProxyHandler(clientSocket, cache, semaphore));
        }
    }
}