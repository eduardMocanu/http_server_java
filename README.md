# 🖧 Java HTTP/1.1 Server from Scratch

This is a multithreaded HTTP/1.1 server built entirely from scratch in Java using low-level sockets and threads — **no frameworks or libraries used**. It supports basic HTTP methods, custom routing, GZIP compression, and file I/O, and includes protections against directory traversal.

---

## 🚀 Features

- ✅ `GET` and `POST` support
- ✅ Persistent connections (`Connection: keep-alive` / `close`)
- ✅ Header parsing and request handling
- ✅ Basic routing:
  - `/echo/<message>` — echoes the message
  - `/user-agent` — returns the user-agent string
  - `/files/<filename>` — GET or POST to read/create files
- ✅ GZIP compression if client supports `Accept-Encoding: gzip`
- ✅ Handles missing headers like `Content-Length` gracefully
- ✅ Prevents directory traversal attacks (`..`, `/`, `\\`)
- ✅ Proper HTTP response codes (200, 201, 400, 404, 405)

---

## 📂 Project Structure

src/main/java/Main.java -> starts the server
src/main/java/HandleClients.java -> handles each client in a thread
test.txt -> test file to retrieve

## ⚙️ How It Works

- Server listens on port 4221.
- For each client connection, it spawns a new thread.
- Parses HTTP request line and headers manually.
- Supports `GET`/`POST` to serve or create .txt files.
- Sends appropriate HTTP response with headers and optional GZIP compression.

- ## 🧪 How to Use
# Compile and Run
javac Main.java HandleClients.java
java Main

🛡️ Security

Prevents malicious path traversal by rejecting requests containing .., /, or \\ in file names.

📘 Notes

The server reads Content-Length to parse POST bodies.

The implementation is educational — real-world servers should rely on well-tested libraries (e.g., Netty, Jetty, Spring Boot).

This server runs one thread per connection. For better scalability, a thread pool can be added.

📄 License

This project is open source and available under the MIT License.

✍️ Author

Edy Mocanu

GitHub: [](https://github.com/MocEddy)

LinkedIn: [](https://www.linkedin.com/in/eduard-mocanu-031803219/)
