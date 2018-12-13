### 构建

```bash
cd src/main/webapp
npm install
cd -
./gradlew bootWar
./gradlew bootScripts
```

### 运行

```bash
cd build/bootScripts
./calendar
```

### 注意
运行时若抛出`ClassNotFoundException`异常，请使用完整的java路径运行，例如：
```cmd
"C:\Program Files\Java\jdk1.8.0_192\bin\java.exe" -jar calendar\build\libs\calendar-0.0.1-SNAPSHOT.war
```
