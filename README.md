# Senior-Project-Hope-Hospital-App

Hope Hospital App is an android application that helps hospitals achieve automation in their basic to mid-level processes which would otherwise require manpower. 
This application has two interfaces:
Admin Interface : This interface is only accessible for the admin users of the application i.e., the hospital employees.
User Interface    : This interface is only accessible for the patients of the hospital.

## (Please scroll down for project screenshots)

### The features include:


#### In User / Patient Interface:

1.	Login / Registration page

2.	Patient Profile page where they can enter and update all the information required in the medical procedure like: Name, Age, Gender, Blood Group, Address, and a Profile Image.

3.	Upload Insurance page where users can upload their insurance card pictures which can be downloaded by the hospital staffs on the admin interface.

4.	Schedule appointment functionality where patients themselves can schedule appointments with doctors only in their wards. While this feature gives flexibility to patients to make an appointment on their own it also restricts them to make appointment with wrong doctors who do not serve in the ward there are appointed in. Therefore, this helps to keep things organized. Patients also have the ability of cancel the appointment if anything comes up.

5.	Reminder feature which sends a reminder notification to patient about their upcoming appointment with a doctor. Patients are sent a notification an hour before their appointment time.

6.	View Test Results feature, where patients can view the pdf version of their test results along with a short message from the Doctor or the hospital.

7.	Instant Notification sent to the patient’s phone for every new test result that have been uploaded to their profile.

8.	A safe encrypted Journal in the application for patients to write about their thoughts and feelings while they might be going through some difficult times in their lives. This journal is encrypted and stored in the server so that nobody can read it except for the patient.

9.	Search for doctors in the hospital and view their profiles to learn about them more.

10.	Current day’s items or lists, if there is any, displayed on the Home Page. For instance, if a patient has an appointment today, it is displayed in the homepage so that the patient does not forget about it.


#### In Admin / Hospital Employees’ Interface:

1.	Login page

2.	Add new doctors to the application.

3.	Update doctor’s profile, add doctors to their specific wards, manage doctor’s schedule, and add availabilities for doctors.

4.	Search for patients. View patients’ profile, add patient to their wards. View and download patient’s insurance cards.

5.	Schedule appointment for patient. Cancel patient’s appointment if needed.

6.	Upload patient’s test results, with short message included about the result.

7.	Create new hospital wards which help in organizing doctors and patients effectively.

8.	Add hospital’s documents like privacy policy, doctor’s policy, etc.


To download the application. You can access open this link in your android phone.
Link: https://drive.google.com/drive/u/0/folders/1LBdj9MVTnl_fwtcvvlO1x1WTox0NxhLr


### Implementation Overview

Hope Hospital App is an android application which is written in JAVA, using Android Studio as the Integrated Development Environment. This application makes use of Google Cloud 
Storage, i.e., Firestore database and Firebase storage to store the data and files of the application users respectively. This application follows event driven application 
architecture which handles and responds to the click events of the users. Therefore, all the classes that are manually written for the application are to respond to actions 
generated by the user. The application uses Firebase libraries to communicate with the database, fetch and store the data as needed in the Firestore database. The application 
also uses other small libraries like Picasso for loading images to image view by proving an image URL and Circle Image View library to make the profile picture of users and 
doctors circular.

![flowchart](https://user-images.githubusercontent.com/61360634/138885042-2a82e6c2-1109-42a4-b3ee-998a52e5dfa2.jpg)

### Screenshots:

### Login Page and Registration page
<p float="left">
  <img src="https://user-images.githubusercontent.com/61360634/138883369-f2226985-a39e-4db6-b063-7825df3ecc6a.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138883487-167eb9eb-f1dc-4293-bdc1-cfdf9f162558.jpg" width="270" height="auto">
</p>

### Admin Home and User Home
<p float="left">
  <img src="https://user-images.githubusercontent.com/61360634/138883512-c1623a9f-8904-4e62-b16b-4f093b494de8.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138888009-42b2fa8c-13ac-495a-a1d0-fd003cd2a78c.jpg" width="270" height="auto">
</p>

### Admin Interface 

<p float="left">
  <img src="https://user-images.githubusercontent.com/61360634/138883512-c1623a9f-8904-4e62-b16b-4f093b494de8.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138883540-eb3a9e3f-c8c1-41b1-a349-696efedbf238.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138883588-2f6b1c25-8825-4e38-a7a7-908561c3652a.jpg" width="270" height="auto">
</p>

<p float="left">
  <img src="https://user-images.githubusercontent.com/61360634/138883615-7acf452d-0bc0-4c33-90d6-dfd6f997a881.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138883693-d91b8248-dada-4f3d-8ac3-4c7070c1631d.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138892146-754f9cfb-67b2-4cb5-95cc-af9648dc4e34.jpg" width="270" height="auto">
</p>

<p float="left">
  <img src="https://user-images.githubusercontent.com/61360634/138883722-8d478a92-17f7-4448-80ba-487142dcebb1.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138883835-1ade36e4-0fe8-4c1d-bc0a-5188c24fca82.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138883875-efa248de-f33f-4a52-89a6-510dd8ff5942.jpg" width="270" height="auto">
</p>

<p float="left">
  <img src="https://user-images.githubusercontent.com/61360634/138883886-30a2da36-f94b-475d-aa02-6c985c9597b0.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138884064-61270559-ca2c-4317-b8da-93a2b07a5984.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138884100-d6bf3968-47e5-4cfb-9d9c-d4d520f62313.jpg" width="270" height="auto">
</p>

### User Interface

<p float="left">
  <img src="https://user-images.githubusercontent.com/61360634/138884141-d6213499-8234-4d86-a7ea-7071ec8f7474.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138884187-3a9962e8-634d-4584-9825-e60bbf2e92ed.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138884206-f95f6df2-f22c-4045-9ec9-b3e58c06b820.jpg" width="270" height="auto">
</p>

<p float="left">
  <img src="https://user-images.githubusercontent.com/61360634/138884247-6178ccdd-d630-4524-b09c-58b276aba32a.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138884314-066acc2f-fb9c-44aa-9e3c-835d7c055c57.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138884343-b2c8fceb-6ada-4670-876e-802bdf245364.jpg" width="270" height="auto">
</p>

<p float="left">
  <img src="https://user-images.githubusercontent.com/61360634/138884462-50f5072c-23d3-4ba1-81b4-832ba88365c0.jpg" width="270" height="auto">
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://user-images.githubusercontent.com/61360634/138884474-eb82c960-6994-44ad-8d38-7b142413dd4c.jpg" width="270" height="auto">
</p>

