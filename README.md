# 🩺 Oncolly

> **Connecting Daily Habits to Clinical Excellence.**

Oncolly is more than just a medical app; it is a **continuous care bridge** between Doctors and Patients. By enabling patients to track their health offline and syncing seamlessly when connected, Oncolly ensures that doctors have the full picture—leading to better diagnosis, timely interventions, and personalized care.

**Empower your patients. Optimize your practice.**

-----

## 🚀 The Tech Stack

Built with a robust, enterprise-grade backend designed for **reliability**, **security**, and **offline-first** mobile experiences.

* **Core:** Java 21 & Spring Boot 3.3
* **Database:** PostgreSQL 15 (Dockerized)
* **Security:** JWT (Stateless) + BCrypt + RBAC (Role-Based Access Control)
* **Architecture:** REST API with Offline-Sync capabilities (UUIDs)
* **Deployment:** Docker & Docker Compose

-----

## 📂 Project Structure

The backend is organized cleanly to separate concerns, ensuring scalability and ease of maintenance for the Android team.

```text
cat.teknos.oncolly
├── 🔐 config       # Security & CORS configuration (The Firewall)
├── 🎮 controllers  # REST API Endpoints (The "Mouth" of the app)
├── 📦 dtos         # Data Transfer Objects (Clean JSON contracts for Android)
├── 🧠 models       # Database Entities (User, Doctor, Patient, Activity)
├── 🗄️ repositories # Database Access Layer (SQL logic & Custom Queries)
├── 🛡️ security     # JWT Filters, Token Generators & Auth Logic
└── 🛠️ utils        # Mappers and Helper classes
```

-----

## ⭐ Key Features

### 1\. 📶 Offline-First Architecture

We understand that health happens everywhere, not just where there is Wi-Fi.

* **UUID Strategy:** All IDs are generated locally on the Android device, preventing sync conflicts when the internet returns.
* **Smart Sync:** The API supports "Delta Syncs," fetching only what changed since the last login to save battery and data.

### 2\. 🛡️ Iron-Clad Security

* **Stateless Authentication:** Uses industry-standard JWTs. No sessions on the server mean faster performance and better scalability.
* **Role Segregation:** Strict firewalls between `DOCTOR` and `PATIENT` data. Doctors can only see patients assigned specifically to them.

### 3\. 🗓️ Conflict-Free Scheduling

* **Smart Appointments:** The backend automatically detects overlaps in the doctor's schedule before confirming a slot, ensuring the doctor is never double-booked.

-----

## ⚡ Quick Start (Dev)

Prerequisites: **Docker** & **Docker Compose**.

1.  **Clone the repo**

    ```bash
    git clone https://github.com/your-username/oncolly.git
    cd oncolly
    ```

2.  **Build & Launch (One Command)**
    This will compile the Java code, build the container, and start PostgreSQL.

    ```bash
    docker-compose up --build
    ```

3.  **Explore the API**
    Once running, access the auto-generated documentation:

    * **Swagger UI:** [http://localhost:8888/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8888/swagger-ui/index.html)

-----

## 🧪 Default Credentials (Seeded Data)

The system auto-seeds with test data on the first run for easy Android testing.

| Role | Email | Password |
| :--- | :--- | :--- |
| **👨‍⚕️ Doctor** | `house@hospital.com` | `doctor123` |
| **🤒 Patient** | `john@patient.com` | `patient123` |

-----

*Oncolly — Because better data means better care.*