# Hackathon Registration Form

A full-stack application for hackathon team registration with problem statement selection, complete with an admin panel for management. Built with React frontend and Spring Boot backend.

## Features

✨ **Team Registration Form**
- Capture team details: name, lead name, email, phone, team size, and institute name
- Client-side validation with detailed error messages
- Multi-step form with progress tracking

📋 **Problem Statement Selection**
- Browse problem statements with pagination
- Real-time selection count display
- Automatic disabling when a problem reaches maximum team selections
- Visual indicators for full problems
- Clean, intuitive card-based UI

🔑 **Admin Panel**
- Secure admin login system
- Upload problem statements from Excel files
- Download reports in Excel format (problem statements, user selections, combined)
- Comprehensive dashboard with statistics

🔄 **Excel Operations**
- Upload problem statements in bulk via Excel
- Download all details in Excel worksheet format
- Multiple report formats available

🔐 **Security**
- Admin authentication
- Session management
- Default admin credentials (changeable)

🎨 **User Interface**
- Modern, gradient-based design
- Responsive layout for mobile and desktop
- Smooth animations and transitions
- Accessibility-focused

## Admin Features

### Admin Login
Access the admin panel with default credentials:
- **Username:** admin
- **Password:** admin@123

### Dashboard
Overview of system capabilities and statistics

### Upload Problems
Upload problem statements from Excel files with the following format:
- Problem ID
- Title
- Description
- Max Users

### Download Reports
- **Problem Statements:** All problems with status
- **User Selections:** All team registrations
- **Combined Report:** Complete multi-sheet report with summary

For detailed admin instructions, see [ADMIN_GUIDE.md](ADMIN_GUIDE.md)

## Project Structure

```
hackathon_form/
├── backend/                           # Spring Boot application
│   ├── src/main/java/com/hackathon/
│   │   ├── admin/
│   │   ├── config/
│   │   │   └── DataInitializer.java
│   │   ├── controller/
│   │   │   ├── AdminController.java
│   │   │   ├── ProblemStatementController.java
│   │   │   └── UserSelectionController.java
│   │   ├── dto/
│   │   │   ├── AdminLoginRequest.java
│   │   │   ├── AdminLoginResponse.java
│   │   │   ├── ApiResponse.java
│   │   │   ├── ProblemStatementDTO.java
│   │   │   └── UserSelectionRequest.java
│   │   ├── model/
│   │   │   ├── Admin.java
│   │   │   ├── ProblemStatement.java
│   │   │   └── UserSelection.java
│   │   ├── repository/
│   │   │   ├── AdminRepository.java
│   │   │   ├── ProblemStatementRepository.java
│   │   │   └── UserSelectionRepository.java
│   │   ├── service/
│   │   │   ├── AdminService.java
│   │   │   ├── ProblemStatementService.java
│   │   │   └── UserSelectionService.java
│   │   └── HackathonFormApplication.java
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
│
└── hackathon-frontend/                # React application
    ├── public/
    ├── src/
    │   ├── components/
    │   │   ├── AdminDashboard.jsx
    │   │   ├── AdminDashboard.css
    │   │   ├── AdminLogin.jsx
    │   │   ├── AdminLogin.css
    │   │   ├── HackathonForm.jsx
    │   │   ├── HackathonForm.css
    │   │   ├── ProblemStatementSelector.jsx
    │   │   ├── ProblemStatementSelector.css
    │   │   ├── UserForm.jsx
    │   │   ├── UserForm.css
    │   │   ├── Pagination.jsx
    │   │   └── Pagination.css
    │   ├── App.jsx
    │   ├── App.css
    │   ├── main.jsx
    │   └── index.css
    ├── package.json
    └── vite.config.js
```

## Getting Started

### Prerequisites

- Node.js and npm (for frontend)
- Java 17+ (for backend)
- Maven
- Database (H2/MySQL configured in `application.yml`)

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The backend will start at `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd hackathon-frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

The frontend will open at `http://localhost:5173` (or as configured in Vite)

## API Endpoints

### Admin Endpoints

**POST** `/api/admin/login`
- Authenticate admin user
- Request body: `{ "username": "admin", "password": "admin@123" }`

**POST** `/api/admin/upload-problems`
- Upload problem statements from Excel file
- Headers: `Content-Type: multipart/form-data`

**GET** `/api/admin/download-problems`
- Download all problem statements as Excel file

**GET** `/api/admin/download-selections`
- Download all user selections as Excel file

**GET** `/api/admin/download-combined-report`
- Download combined report with all details

### Problem Statements

**GET** `/api/problem-statements`
- Fetch paginated problem statements
- Query params: `page` (default: 0), `size` (default: 10)

**GET** `/api/problem-statements/{id}`
- Fetch a specific problem statement

### User Selections

**POST** `/api/user-selections`
- Submit team registration with problem selection

**GET** `/api/user-selections`
- Fetch all user selections (paginated)

## Database Schema

### Admin Table
```sql
CREATE TABLE admins (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP,
  last_login_at TIMESTAMP
);
```

### Problem Statement Table
```sql
CREATE TABLE problem_statements_6702 (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  problem_id VARCHAR(255) UNIQUE NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  max_users INT NOT NULL,
  current_users INT NOT NULL,
  is_available BOOLEAN NOT NULL
);
```

### User Selection Table
```sql
CREATE TABLE user_selections_6702 (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id VARCHAR(255) UNIQUE NOT NULL,
  team_name VARCHAR(255) NOT NULL,
  team_lead_name VARCHAR(255) NOT NULL,
  team_lead_email VARCHAR(255) UNIQUE NOT NULL,
  problem_statement_id BIGINT NOT NULL,
  created_at TIMESTAMP,
  FOREIGN KEY (problem_statement_id) REFERENCES problem_statements_6702(id)
);
```

## Configuration

### Backend Configuration (application.yml)

```yaml
spring:
  application:
    name: hackathon-form-backend
  
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  
  h2:
    console:
      enabled: true

server:
  port: 8080

logging:
  level:
    root: INFO
```

## Deployment (Render + Docker)

This repository includes deployment essentials for Render and Docker:
- `render.yaml` for Render Blueprint deployment
- `backend/Dockerfile` for Spring Boot backend
- `frontend/Dockerfile` for containerized frontend build
- `docker-compose.yml` for local full-stack containers

### Deploy to Render

1. Push code to GitHub
2. In Render, create a **Blueprint** from this repository
3. Render will provision PostgreSQL, backend service, and frontend static site

Set these environment values after first deploy:
- `CORS_ALLOWED_ORIGINS=https://<your-frontend>.onrender.com`
- `VITE_API_BASE_URL=https://<your-backend>.onrender.com/api`

Detailed steps: see `DEPLOY_RENDER.md`

### Run locally with Docker

```bash
docker compose up --build
```

Local URLs:
- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080/api`

## Excel File Format

### For Uploading Problem Statements

| Column 1 | Column 2 | Column 3 | Column 4 |
|----------|----------|----------|----------|
| Problem ID | Title | Description | Max Users |
| PROB_001 | AI Chatbot | Build an AI chatbot using NLP... | 2 |
| PROB_002 | ML Pipeline | Create a machine learning... | 2 |
| PROB_003 | IoT Solution | Develop an IoT-based system... | 2 |

## Technologies Used

### Backend
- Spring Boot 3.1.5
- Spring Data JPA
- Apache POI (for Excel operations)
- H2 / MySQL Database
- Lombok
- Maven

### Frontend
- React 19
- Axios (HTTP client)
- Vite (build tool)
- CSS3 (styling)

## Security Considerations

1. **Change Default Credentials:** After first deployment, change admin credentials
2. **CORS Configuration:** Update CORS settings for production
3. **Password Hashing:** Consider implementing bcrypt for password hashing
4. **File Upload Validation:** Only Excel files are accepted; implement size limits
5. **Session Management:** Implement token-based authentication (JWT) for production

## Future Enhancements

- [ ] JWT-based authentication
- [ ] Role-based access control (RBAC)
- [ ] Email notifications
- [ ] Advanced analytics and charts
- [ ] Bulk operations
- [ ] Audit logging
- [ ] Two-factor authentication
- [ ] Password reset functionality

## Troubleshooting

### Backend Issues
- Ensure Java 17+ is installed
- Check if port 8080 is available
- Verify Maven is installed

### Frontend Issues
- Clear node_modules and reinstall: `rm -rf node_modules && npm install`
- Clear npm cache: `npm cache clean --force`

### Database Issues
- Check H2 console at `http://localhost:8080/h2-console`
- Verify datasource configuration in application.yml

## Support

For issues or questions, please refer to the [ADMIN_GUIDE.md](ADMIN_GUIDE.md) for admin-specific documentation.

## License

This project is provided as-is for hackathon purposes.

    "instituteName": "Institute Name",
    "problemStatementId": 1
  }
  ```

**GET** `/api/user-selections/{userId}`
- Get a user's selection

**DELETE** `/api/user-selections/{userId}`
- Remove a user's selection

## Data Models

### ProblemStatement
```java
- id: Long (Primary Key)
- title: String
- description: String (TEXT)
- category: String
- currentUsers: Integer (tracks teams selected)
- maxUsers: Integer (always 2)
- isAvailable: Boolean
```

### UserSelection
```java
- id: Long (Primary Key)
- teamName: String
- teamLeadEmail: String
- teamLeadPhone: String
- numberOfMembers: String
- instituteName: String
- problemStatementId: Long (Foreign Key)
- createdAt: LocalDateTime
```

## Frontend Features Explained

### Step 1: Team Details Form
- Validates all required fields
- Email format validation
- Phone number validation (minimum 10 digits)
- Team size selection (1-10 members)
- Real-time error clearing

### Step 2: Problem Selection
- Displays 10 problems per page
- Shows selection count next to each problem (e.g., "1/2", "2/2")
- Automatically disables problem cards when 2 teams select them
- Visual "FULL" badge on disabled problems
- Selected problem summary at the bottom
- Loading state while fetching data

## Key Implementation Details

### Selection Logic
1. When a form is submitted, the backend checks if the selected problem has less than 2 team selections
2. If available, the `currentUsers` count is incremented
3. If already selected by 2 teams, the frontend disables the card and shows "FULL" badge
4. The count updates in real-time as teams make selections

### Pagination
- 75 problem statements total (can be changed in DataInitializer)
- 10 items per page
- Smart pagination showing: First → Previous ← → Current ← → Next → Last
- Ellipsis (...) for gaps between pages

### Validation
- Frontend: Client-side validation for better UX
- Backend: Server-side validation for data integrity
- Both prevent invalid submissions

## Configuration

### Backend (`application.yml`)
```yaml
spring:
  application:
    name: hackathon-form-backend
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
  h2:
    console:
      enabled: true
  cors:
    allowedOrigins: http://localhost:3000
```

### Frontend (Axios Configuration)
Base URL: `http://localhost:8080`
All requests include proper CORS headers

## Build and Deployment

### Frontend Build
```bash
cd frontend
npm run build
```
Creates optimized production build in `build/` directory

### Backend Build
```bash
cd backend
mvn clean install
mvn package -DskipTests
```
Creates JAR file in `target/` directory

## Testing

### Frontend
```bash
cd frontend
npm test
```

### Backend
```bash
cd backend
mvn test
```

## Troubleshooting

**CORS Error**: Ensure backend is running on port 8080 and `@CrossOrigin` is configured
**Port Already in Use**: Change ports in backend config or frontend `.env`
**Database Errors**: Verify database configuration in `application.yml`
**Form Not Submitting**: Check browser console for errors and verify backend API endpoint

## Future Enhancements

- User authentication and authorization
- Team member list management
- Email confirmation
- Problem statement search/filter
- Export registration data to CSV/Excel
- Admin dashboard for monitoring
- Real-time synchronization with WebSocket
- PDF certificate generation

## License

This project is open source and available under the MIT License.

## Contact

For support, please reach out to the hackathon organizers.
#   H a c k a t h o n D e m o M a i n  
 