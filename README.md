# NiveshCore360 - Enterprise AI-Powered Investment Management System

NiveshCore360 is a production-grade, premium desktop investment portfolio management application built exclusively on the Java 21 platform. Leveraging Spring Boot 3, Hibernate JPA, and Java Swing (FlatLaf), it delivers real-time risk analytics, Newton-Raphson financial solvers, AI-driven portfolio advisory, and secure Multi-Factor Authentication.

---

## 🚀 Key Features

*   **Polymorphic Asset Portfolio Management**: Map stocks, mutual funds, gold, cryptos, and fixed income instruments under a polymorphic `Asset` database model.
*   **Newton-Raphson XIRR & Volatility Solver**: Custom mathematical equations calculating CAGR, Sharpe ratios, portfolio beta, standard deviations, and forecasted compound growth paths.
*   **Interactive AI Wealth Advisor**: Persistent conversational dialog interface querying GPT-4 models to suggest rebalancing targets and generate advisory PDFs.
*   **Google Authenticator MFA**: Security configuration including base32 TOTP OTPs, JWT token rotations, and encrypted security filters.
*   **Premium Glassmorphic UI**: High-fidelity dark mode layouts utilizing `LogoPainter` anti-aliased vector arcs, card components, and interactive onboarding wizards.
*   **Web-in-Browser Desktop VNC**: Pre-packaged Docker container wrapper running `Xvfb`, `Fluxbox`, and `noVNC`, enabling the client desktop application to run in any standard web browser.

---

## 🛠️ Technology Stack

*   **Language**: Java 21 (OpenJDK)
*   **Backend & IOC**: Spring Boot 3.2, Spring Security, Spring Data JPA, Hibernate ORM
*   **UI/UX Framework**: Java Swing (FlatLaf Dark Theme + Vector Graphic Canvas)
*   **Visualizations**: JFreeChart (Dynamic allocation pie diagrams and historical performance plots)
*   **Document Generators**: OpenPDF (Automatic PDF advisory statements rendering)
*   **Database**: H2 (In-memory development mode) & MySQL 8 (Production profile container)
*   **CI/CD & DevOps**: Docker, Docker Compose, GitHub Actions pipelines

---

## 📦 Directory Structure

```text
NiveshCore360
├── .github/workflows       # GitHub Actions automated maven test execution
├── Dockerfile              # Multi-stage JVM runtime with VNC server wrapper
├── docker-compose.yml      # DB and application container stack orchestrations
├── pom.xml                 # Maven dependency manifests (Java 21, Spring Boot 3)
└── src
    ├── main
    │   ├── java/com/niveshcore360
    │   │   ├── config      # Database seeders and security filters
    │   │   ├── constants   # Typography tokens and color variables
    │   │   ├── controller  # MVC controllers mapping views to transactions
    │   │   ├── dto         # Data Transfer Objects
    │   │   ├── entity      # JPA database entity mappings (unified Assets)
    │   │   ├── repository  # Spring Data JPA repository layers
    │   │   ├── security    # TOTP generators, JWT providers, session state context
    │   │   ├── service     # AI services, PDF generators, calculators, portfolios
    │   │   ├── util        # Newton-Raphson XIRR and standard Sharpe calculators
    │   │   └── view        # Custom Swings panels (Dashboard, Advisor, Onboarding)
    │   └── resources
    │       └── application.properties # H2/MySQL profiles mappings
    └── test                # Mockito and JUnit 5 service validation suites
```

---

## ⚙️ Setup & Installation

### Option 1: Running in Docker Stack (Recommended)
Build and run the entire suite—including the MySQL database instance and the virtual display wrapper—with a single command:
```bash
docker-compose up --build
```
Once the containers are running:
*   Open your web browser and navigate to: **`http://localhost:8080`**
*   Click **Connect** to access the NiveshCore360 Swing desktop interface rendered inside the browser.

### Option 2: Running Native Desktop Client
To run the Swing desktop app natively on your host machine:

1. Ensure you have **Java 21 (or later)** and **Maven** installed.
2. Clone the repository and navigate to the project directory:
   ```bash
   git clone https://github.com/samikshanimje/NiveshCore360.git
   cd NiveshCore360
   ```
3. Compile and execute the application:
   ```bash
   mvn spring-boot:run
   ```

---

## 🔑 Default Seed Credentials

Upon database initialization, the system seeds two default test accounts:

*   **System Administrator**:
    *   *Username*: `admin`
    *   *Password*: `admin123`
*   **Standard Client**:
    *   *Username*: `user`
    *   *Password*: `user123`

---

## 🔒 Security & Mathematical Compliance

*   **TOTP Generation**: Implements a standard cryptographic SHA-1 byte matrix validating Google Authenticator codes.
*   **XIRR Computation**: Solves non-periodic cashflows using numerical Newton-Raphson iterations:
    $$f(r) = \sum_{i=1}^{N} \frac{C_i}{(1 + r)^{\frac{d_i - d_1}{365}}}$$
    Finding the root $r$ where $f(r) = 0$.
*   **Sharpe Ratio Engine**: Calculates risk-adjusted excess returns over a risk-free rate relative to portfolio volatility:
    $$\text{Sharpe} = \frac{R_p - R_f}{\sigma_p}$$
