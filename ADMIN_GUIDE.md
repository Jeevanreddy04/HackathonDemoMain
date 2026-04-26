# Admin Panel Guide

## Overview
The Admin Panel allows administrators to manage problem statements and download reports related to hackathon team registrations.

## Default Admin Credentials
- **Username:** admin
- **Password:** admin@123

**Important:** Change these credentials in the database after first login for security.

## Admin Features

### 1. Dashboard
The dashboard provides an overview of the system capabilities and quick information about the Excel upload format.

### 2. Upload Problem Statements
Upload problem statements from a Microsoft Excel file.

#### Excel File Format
Your Excel file should contain the following columns:

| Column | Description | Example |
|--------|-------------|---------|
| Problem ID | Unique identifier for the problem | PROB_001 |
| Title | Problem statement title | AI-Powered Chatbot |
| Description | Detailed description of the problem | Build a chatbot using AI that can... |
| Max Users | Maximum number of teams allowed | 2 |

#### Steps to Upload:
1. Navigate to the "Upload Problems" tab
2. Select an Excel file (.xlsx or .xls)
3. Click "Upload"
4. System will display success/failure message
5. Existing problems will be updated if Problem IDs match

#### Sample Excel Structure:
```
Problem ID | Title | Description | Max Users
-----------|-------|-------------|----------
PROB_001 | AI Chatbot | Build an intelligent... | 2
PROB_002 | ML Pipeline | Create a ML... | 2
PROB_003 | IoT Solution | Develop an IoT... | 2
```

### 3. Download Reports

#### Available Reports:

**Problem Statements**
- Contains all problem statements with current status
- Includes: Problem ID, Title, Description, Max Users, Current Users, Availability

**User Selections**
- Contains all team registrations and their problem selections
- Includes: User ID, Team Name, Team Lead Name, Email, Selected Problem, Selection Date

**Complete Report**
- Comprehensive multi-sheet report
- Sheet 1: Problem Statements
- Sheet 2: User Selections
- Sheet 3: Summary with statistics

#### Usage:
1. Navigate to "Download Reports" tab
2. Click on the desired report button
3. File will be downloaded automatically

## API Endpoints

### Authentication
```
POST /api/admin/login
Body: { "username": "admin", "password": "admin@123" }
Response: { "adminId": 1, "username": "admin", "fullName": "Admin User", "email": "admin@hackathon.com", "success": true }
```

### Upload Problems
```
POST /api/admin/upload-problems
Body: FormData with file
Headers: Content-Type: multipart/form-data
Response: { "message": "Successfully uploaded X problem statements", "success": true }
```

### Download Problem Statements
```
GET /api/admin/download-problems
Response: Excel file (.xlsx)
```

### Download User Selections
```
GET /api/admin/download-selections
Response: Excel file (.xlsx)
```

### Download Combined Report
```
GET /api/admin/download-combined-report
Response: Excel file (.xlsx)
```

## Security Notes

1. **Change Default Credentials:** After first login, update the admin password in the database
2. **CORS:** Currently CORS is enabled for all origins. Restrict this in production.
3. **File Upload:** Only Excel files (.xlsx, .xls) are accepted for upload
4. **Data Validation:** The system validates required fields before uploading

## Troubleshooting

### Upload Fails
- Ensure Excel file has all required columns
- Check that all rows have valid data
- Use .xlsx format (recommended over .xls)

### Download Fails
- Check internet connection
- Try a different browser
- Clear browser cache

### Login Issues
- Verify username and password
- Check if admin account is active in database

## Backend Requirements

The backend should be running on `http://localhost:8080` for the admin panel to work correctly.

## Environment Variables

No specific environment variables needed for admin panel. Ensure:
- Spring Boot server is running
- Database is initialized
- CORS is properly configured

## Future Enhancements

- [ ] Add admin user management (create/edit/delete admins)
- [ ] Implement role-based access control
- [ ] Add email notifications for new registrations
- [ ] Create advanced analytics and charts
- [ ] Add bulk operations for problem statements
- [ ] Implement audit logging
