# 🛡️ Digital Insurance Management System

A robust and full-featured platform for managing digital insurance policies, claims, renewals, and customer support. Built with modern web technologies for scalability, security, and ease of use for both users and administrators.

---

## ✨ Key Features

* 🔐 **User Management**
  Secure registration, login, and role-based access for Users and Admins using JWT and Spring Security.

* 📄 **Policy Management**
  Users can browse, purchase, and renew insurance policies. Admins can manage policies (CRUD).

* 💰 **Claim Management**
  Policyholders can submit and track insurance claims. Admins can review, approve/reject, and manage claim statuses.

* 🧾 **Support Ticket System**
  Users can raise support tickets related to policies or claims. Admins can respond, update, and resolve them.

* 📊 **Admin Dashboard**
  Centralized control panel to manage all users, policies, claims, and support tickets.

---

## 🛠️ Tech Stack

### Backend

* **Java 17**, **Spring Boot**
* **Spring Security**, **JWT** for secure authentication
* **Spring Data JPA**, **Hibernate**
* **MySQL** (configured via Docker)
* **JUnit**, **Mockito**, **H2** for testing
* **Maven** for build and dependency management

### Frontend

* **Vue.js 3** with **TypeScript**
* **Vite** as the build tool
* **Tailwind CSS** for modern UI styling
* **Axios** for API requests

---

## 📁 Project Structure

```
digital_insurance_mngmntSystem/
│
├── backend/     # Spring Boot application
│
├── frontend/    # Vue 3 + TypeScript frontend
```

---

## 🚀 Getting Started

### 🔧 Prerequisites

* Java 17+
* Node.js (v16+ recommended) and npm/yarn
* MySQL Server (or use Docker setup)

---

### 🖥️ Running the Application

#### Option 1: Manual Setup (Dev Mode)

1. **Clone the repository**

```bash
git clone <repository-url>
cd digital_insurance_mngmntSystem
```

2. **Backend Setup**

```bash
cd backend
# Update `application.properties` or use `.env`
./mvnw clean package
./mvnw spring-boot:run
```

3. **Frontend Setup**

```bash
cd frontend
npm install      # or yarn install
npm run dev      # or yarn dev
```

---


* Frontend: [http://localhost:5173](http://localhost:5173)
* Backend: [http://localhost:8081](http://localhost:8081)

---

## 📚 API Overview

API endpoints are RESTful and documented across:

* `/auth`: Register/Login
* `/policies`: View/Create/Update/Delete policies
* `/user/policy`: Purchase, view, or update user's policies
* `/policy/{id}/renew`: Renew a purchased policy
* `/claim`: Submit, view, update, or delete claims
* `/support`: Create/view/update/delete support tickets

Refer to [Project Documentation](./documentation.md) for full endpoint definitions with examples.

---

## 🧪 Testing

* Backend tests with **JUnit**, **Mockito**, and **H2**.
* Frontend uses **vue-tsc** for type safety (manual testing currently supported).

---


## 🔮 Future Enhancements

* 2FA for enhanced security
* Payment gateway integration
* Automated claim approval workflows
* AI-powered policy recommendations
* Chatbot-based support system
* Integration with third-party data services

---


Thank you for checking out the **Digital Insurance Management System**!
We hope this platform serves as a foundation for building smart, secure, and scalable insurance applications.
