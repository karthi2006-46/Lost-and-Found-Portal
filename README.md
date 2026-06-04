<img width="100%" src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=6,11,20&height=180&section=header&text=Lost+and+Found+Portal&fontSize=42&fontColor=fff&animation=twinkling&fontAlignY=32&desc=Full-Stack+Campus+Lost+and+Found+Management+System&descAlignY=62&descSize=16"/>

<div align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white"/>
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=css3&logoColor=white"/>
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black"/>
  <br/><br/>
  <a href="https://lostfoundportal.in">
    <img src="https://img.shields.io/badge/Live%20Demo-lostfoundportal.in-38BDAE?style=for-the-badge&logo=googlechrome&logoColor=white"/>
  </a>
  &nbsp;
  <a href="https://github.com/karthi2006-46/lost-and-found">
    <img src="https://img.shields.io/badge/GitHub-Repository-181717?style=for-the-badge&logo=github&logoColor=white"/>
  </a>
</div>

---

## About The Project

The **Lost and Found Portal** is a full-stack web application built to digitize and streamline the process of reporting, searching, and claiming lost items within a college campus. Instead of relying on notice boards or word-of-mouth, students and staff can log in, post lost/found items with details and images, and search the database to recover their belongings.

---

## Features

- **Report Lost / Found Items** — Submit item details with description and location
- **Search and Filter** — Browse reported items by category, date, or keyword
- **User Authentication** — Secure login and registration for students/staff
- **Claim Requests** — Users can raise a claim on found items
- **Admin Dashboard** — Manage all listings, users, and claim statuses
- **Responsive Design** — Works seamlessly on desktop and mobile

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Java, Spring Boot, REST APIs |
| **Frontend** | HTML5, CSS3, JavaScript |
| **Database** | MySQL |
| **Version Control** | Git, GitHub |
| **IDE** | Eclipse, VS Code |

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- MySQL 8+
- Git

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/karthi2006-46/lost-and-found.git
cd lost-and-found

# 2. Configure the database
# Open src/main/resources/application.properties and set:
# spring.datasource.url=jdbc:mysql://localhost:3306/lostfound_db
# spring.datasource.username=YOUR_USERNAME
# spring.datasource.password=YOUR_PASSWORD

# 3. Create the database
mysql -u root -p -e "CREATE DATABASE lostfound_db;"

# 4. Build and run
mvn spring-boot:run
```

> The app will be live at `http://localhost:8080`

---

## Project Structure

```
lost-and-found/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/lostfound/
│   │   │       ├── controller/     # REST Controllers
│   │   │       ├── model/          # Entity Classes
│   │   │       ├── repository/     # JPA Repositories
│   │   │       └── service/        # Business Logic
│   │   └── resources/
│   │       ├── static/             # HTML, CSS, JS
│   │       └── application.properties
└── pom.xml
```

---

## Live Demo

> **[lostfoundportal.in](https://lostfoundportal.in)**

---

## Author

<div align="center">
  <strong>Karthikeyan R R</strong><br/>
  BCA Student &middot; Full-Stack Java Developer<br/>
  Dr. M.G.R. Educational and Research Institute, Chennai<br/><br/>
  <a href="mailto:rr.karthikeyan2006@gmail.com">
    <img src="https://img.shields.io/badge/Gmail-rr.karthikeyan2006%40gmail.com-D14836?style=flat-square&logo=gmail&logoColor=white"/>
  </a>
  <a href="https://linkedin.com/in/karthikeyan-rr">
    <img src="https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=flat-square&logo=linkedin&logoColor=white"/>
  </a>
  <a href="https://github.com/karthi2006-46">
    <img src="https://img.shields.io/badge/GitHub-karthi2006--46-181717?style=flat-square&logo=github&logoColor=white"/>
  </a>
</div>

---

<img width="100%" src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=6,11,20&height=120&section=footer&animation=twinkling"/>
