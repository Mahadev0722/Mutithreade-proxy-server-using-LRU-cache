Below is the **README.md** file for your project, tailored to include all relevant details about the multi-threaded proxy server. You can copy and paste this directly into your GitHub repository.

---

# Multi-Threaded Proxy Server with LRU Cache

![Java](https://img.shields.io/badge/Java-17-blue) ![License](https://img.shields.io/badge/License-MIT-green)

This project implements a **multi-threaded HTTP proxy server** in Java with an **LRU (Least Recently Used) cache**. The proxy server forwards client requests to the appropriate web servers, caches responses for faster retrieval, and handles multiple concurrent connections using threads. It also gracefully handles missing resources like `favicon.ico`.

---

## Table of Contents

1. [Features](#features)
2. [Prerequisites](#prerequisites)
3. [Installation](#installation)
4. [Usage](#usage)
5. [Code Structure](#code-structure)
6. [Troubleshooting](#troubleshooting)
7. [Contributing](#contributing)
8. [License](#license)

---

## Features

- **Multi-threading**: Handles multiple client connections concurrently using a thread pool.
- **LRU Cache**: Implements an LRU cache to store and serve frequently accessed responses efficiently.
- **Graceful Error Handling**: Returns `404 Not Found` for missing resources instead of crashing.
- **Logging**: Logs incoming requests and their outcomes for debugging and monitoring.
- **Customizable**: Easily adjust cache size, thread pool size, and server port.

---

## Prerequisites

Before running the proxy server, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 17 or higher.
  - Download from [Oracle's official website](https://www.oracle.com/java/technologies/javase-downloads.html) or use OpenJDK.
- **Text Editor or IDE**: Any text editor (e.g., VS Code, Sublime Text) or an Integrated Development Environment (IDE) like IntelliJ IDEA or Eclipse.
- **Internet Connection**: Required for forwarding requests to external servers.

---

## Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/<your-username>/multi-threaded-proxy-server.git
   cd multi-threaded-proxy-server
   ```

2. **Compile the Code**:
   - Navigate to the project directory and compile the Java files:
     ```bash
     javac MultiThreadedProxyServer.java
     ```

3. **Run the Proxy Server**:
   - Start the proxy server:
     ```bash
     java MultiThreadedProxyServer
     ```

---

## Usage

1. **Configure Your Browser**:
   - Set up your browser to use the proxy server:
     - **Proxy Type**: Manual
     - **HTTP Proxy**: `127.0.0.1`
     - **Port**: `8080`

2. **Test the Proxy Server**:
   - Open your browser and visit any website (e.g., `http://example.com`).
   - The proxy server will log the request in the terminal and forward it to the actual server.
   - Subsequent requests to the same URL will be served from the cache.

3. **Monitor Logs**:
   - The terminal will display logs for each request, including:
     ```
     Proxy server started on port 8080
     Accepted connection from /127.0.0.1
     Handling request for URL: http://example.com
     Serving cached response for URL: http://example.com
     ```

---

## Code Structure

The project consists of the following classes:

1. **LRUCache**:
   - Implements an LRU cache using a doubly linked list and a hash map.
   - Manages the most recently used items and evicts the least recently used ones when the cache is full.

2. **Node**:
   - Represents a node in the doubly linked list used by the LRU cache.

3. **DoublyLinkedList**:
   - A helper class for managing the doubly linked list used in the LRU cache.

4. **ProxyHandler**:
   - Handles individual client requests in separate threads.
   - Fetches responses from the web server, caches them, and sends them back to the client.

5. **MultiThreadedProxyServer**:
   - The main class that initializes the server, manages the thread pool, and accepts client connections.

---

## Troubleshooting

1. **FileNotFoundException**:
   - If you see errors like `java.io.FileNotFoundException: http://example.com/favicon.ico`, it means the requested resource is missing. The server already handles this gracefully by returning a `404 Not Found` response.

2. **Port Already in Use**:
   - If the port `8080` is already in use, change the port in the code:
     ```java
     int port = 9090; // Replace 8080 with another port number
     ```

3. **Firewall Blocking**:
   - Ensure your firewall or antivirus software is not blocking the proxy server.

4. **Deprecated API Warning**:
   - During compilation, you may see a warning about deprecated APIs. Recompile with the `-Xlint:deprecation` flag to identify and address deprecated methods:
     ```bash
     javac -Xlint:deprecation MultiThreadedProxyServer.java
     ```

---

## Contributing

Contributions are welcome! If you'd like to improve this project, follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeatureName`).
3. Commit your changes (`git commit -m "Add your feature"`).
4. Push to the branch (`git push origin feature/YourFeatureName`).
5. Open a pull request.

For major changes, please open an issue first to discuss your proposed changes.

---

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## Acknowledgments

- Inspired by the need for a simple, educational proxy server implementation.
- Thanks to the Java community for providing robust libraries and tools.

---

Feel free to customize this README further based on your specific requirements or additional features you plan to add. Let me know if you need help with anything else!
