# Quick Start Guide - Admin Panel

## 🚀 Getting Started

### Step 1: Start the Application

**Backend:**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
Backend runs on: `http://localhost:8080`

**Frontend (in a new terminal):**
```bash
cd hackathon-frontend
npm install
npm run dev
```
Frontend runs on: `http://localhost:5173`

### Step 2: Access Admin Panel

1. Open the user registration page in your browser
2. Click the **🔐 Admin** button (purple circle at bottom-right)
3. Enter default credentials:
   - **Username:** `admin`
   - **Password:** `admin@123`
4. Click "Login"

## 📊 Admin Panel Sections

### Dashboard Tab
- Overview of system capabilities
- Information about Excel upload format
- Quick reference for admin features

### Upload Problems Tab
1. Prepare an Excel file with these columns:
   - **Column A:** Problem ID (e.g., PROB_001)
   - **Column B:** Title (e.g., AI Chatbot)
   - **Column C:** Description (detailed problem statement)
   - **Column D:** Max Users (number, e.g., 2)

2. Click the upload area or drag & drop file
3. Click "Upload" button
4. Wait for success message

### Download Reports Tab

Three report options available:

| Report | Contains |
|--------|----------|
| **Problem Statements** | All problems with status, max users, current users |
| **User Selections** | All team registrations and their selected problems |
| **Complete Report** | Multi-sheet Excel with problems, selections, and summary |

## 📋 Excel Upload Format Example

```
Problem ID      | Title                  | Description                        | Max Users
----------------|------------------------|------------------------------------|-----------
PROB_001        | AI-Powered Chatbot     | Build an intelligent chatbot...    | 2
PROB_002        | ML Pipeline            | Create a machine learning...       | 2
PROB_003        | IoT Smart City         | Develop IoT solutions...           | 2
PROB_004        | Blockchain App         | Build blockchain application...    | 2
PROB_005        | Cloud Migration        | Migrate legacy systems to cloud... | 2
```

## 🔐 Security Notes

**Change default credentials immediately!**

To change admin credentials, you need to update the database directly. Example SQL:
```sql
UPDATE admins SET password = 'your_new_password' WHERE username = 'admin';
```

## ⚡ Common Tasks

### Upload 50 Problem Statements
1. Prepare Excel file with 50 rows
2. Go to Upload Problems tab
3. Select file and upload
4. Done! All 50 problems are now in the system

### Download All Team Registrations
1. Go to Download Reports tab
2. Click "User Selections" button
3. Open downloaded Excel file
4. View all team registrations with emails

### Get Complete Summary
1. Go to Download Reports tab
2. Click "Complete Report" button
3. Open file with 3 sheets:
   - Problem Statements
   - User Selections
   - Summary with statistics

## 🐛 Troubleshooting

### Upload Not Working
- ✓ Ensure file is .xlsx or .xls format
- ✓ Check all required columns are present
- ✓ Verify no cells are empty in required columns
- ✓ Check backend is running on port 8080

### Download Not Working
- ✓ Check internet connection
- ✓ Try different browser
- ✓ Check browser download permissions

### Login Failed
- ✓ Verify credentials are correct
- ✓ Check if backend is running
- ✓ Clear browser localStorage and try again

## 📞 Support

For detailed documentation, see:
- [ADMIN_GUIDE.md](ADMIN_GUIDE.md) - Comprehensive admin guide
- [README.md](README.md) - Full project documentation
- [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - Technical implementation details

## 🎯 Key Features Summary

✅ **Admin Authentication** - Secure login system
✅ **Excel Upload** - Bulk import problem statements
✅ **Excel Download** - Export data in Excel format
✅ **Multiple Reports** - Problem statements, selections, combined
✅ **Session Management** - Persistent login state
✅ **Responsive UI** - Works on desktop and mobile
✅ **Data Validation** - Automatic error detection
✅ **Professional Design** - Modern gradient-based UI

---

**Happy Administrating! 🎉**
