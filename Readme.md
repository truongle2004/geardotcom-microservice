## 🧭 Summary: JVM Timezone Configuration

| Environment | How to Set | Example / Notes |
|--------------|-------------|------------------|
| **Windows (per app)** | JVM argument | `java -Duser.timezone=Asia/Ho_Chi_Minh -jar app.jar` |
| **Windows (global)** | Environment variable | Add `JAVA_TOOL_OPTIONS=-Duser.timezone=Asia/Ho_Chi_Minh` in System Environment Variables |
| **Linux / macOS** | Shell export or JVM argument | `export JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Ho_Chi_Minh"` |
| **Dockerfile** | ENV + JVM option | ```dockerfile\nENV TZ=Asia/Ho_Chi_Minh\nENV JAVA_OPTS="-Duser.timezone=Asia/Ho_Chi_Minh"``` |
| **Docker Compose** | Compose environment | ```yaml\nenvironment:\n  TZ: Asia/Ho_Chi_Minh\n  JAVA_OPTS: "-Duser.timezone=Asia/Ho_Chi_Minh"``` |
| **Spring Boot (optional)** | Application config | `spring.jackson.time-zone=Asia/Ho_Chi_Minh` *(for JSON date serialization)* |

---

✅ **Recommended default:**  
Always start your Java or Spring Boot app with:
```bash
java -Duser.timezone=Asia/Ho_Chi_Minh -jar app.jar
