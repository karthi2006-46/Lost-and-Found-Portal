Perfect 👍
Here is your **final README.md file**.
Just copy this and save it as **README.md** in your project root folder.

---

# 🧳 Lost & Found Portal – Java Full Stack Project

A web-based Lost & Found Portal that allows students to report lost and found items in colleges.
Built using **Spring Boot, MySQL, HTML, CSS, Bootstrap & JavaScript**.

---

## 🚀 Features

* User Registration & Login
* JWT Authentication
* Report Lost / Found Items
* Upload Item Photo
* View Item Details
* Admin Verification (optional)
* Secure API Access

---

## 🛠 Technologies Used

| Layer          | Technology                         |
| -------------- | ---------------------------------- |
| Frontend       | HTML, CSS, Bootstrap, JavaScript   |
| Backend        | Java, Spring Boot, Spring Security |
| Database       | MySQL                              |
| Authentication | JWT                                |
| Build Tool     | Maven                              |

---

## 💻 How To Run Locally

### Step 1: Install Software

* Java JDK 17+
* MySQL Server
* Apache Maven
* VS Code / IntelliJ IDEA

---

### Step 2: Create Database

```sql
CREATE DATABASE lostfound;
```

---

### Step 3: Configure Database

Edit `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lostfound
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

---

### Step 4: Build Project

Open terminal in project folder:

```bash
mvn clean install
```

---

### Step 5: Run Server

```bash
mvn spring-boot:run
```

or

```bash
java -jar target/lostfound-0.0.1-SNAPSHOT.jar
```

---

### Step 6: Open Website

```
http://localhost:8080/
```

---

### Step 7: Register & Login

* Register a new user
* Login to get access token
* Report lost/found items
* View items with photos

---

### Step 8: Make Admin (Optional)

```sql
UPDATE users SET role='ADMIN' WHERE username='yourusername';
```

---

## 📁 Project Structure

```
src/
 └── main/
     ├── java/com/example/lostfound/
     ├── resources/static/
     └── resources/application.properties
```

-
