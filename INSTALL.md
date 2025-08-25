
# Installation Instructions

## Prerequisites
- Java Development Kit (JDK) 17+ installed and available in your PATH.
- [GraalVM](https://www.graalvm.org/) installed (for native executable build).
- Ensure `native-image` is installed (use `gu install native-image` if missing).

## Project Structure
```
gofundme/
├── gofundme/                # Your package with Java source files
│   ├── GoFundMeProcessor.java
│   └── (other classes)
└── input.txt                # Example input file
```

## Steps to Build and Run

### 1. Compile the Java Files
From the project root (one level above `gofundme/` folder):
```bash
javac -d out gofundme/*.java
```

### 2. Run as a Java Application
```bash
java -cp out gofundme.GoFundMeProcessor input.txt
```

### 3. Package into a JAR
```bash
jar --create --file gofundme.jar --main-class gofundme.GoFundMeProcessor -C out .
```

Run the JAR:
```bash
java -jar gofundme.jar input.txt
```

### 4. Build Native Executable with GraalVM
```bash
native-image -cp out gofundme.GoFundMeProcessor -H:Name=gfm-recurring
```

This will create a native executable named `gofundme`.

Run it:
```bash
./gfm-recurring input.txt
```

Or via pipe:
```bash
cat input.txt | ./gfm-recurring
```

## Notes
- Ensure file paths are correct if using relative paths.
- For Windows, use `gfm-recurring.exe` after building the native image.
- Recompile using:
```bash
javac -Xlint:unchecked -d out gofundme/*.java
```
to see warnings for unchecked operations.

---
**Maintainer:** Your Name  
**Version:** 1.0.0  
