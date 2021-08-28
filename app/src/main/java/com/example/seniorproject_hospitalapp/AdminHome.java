package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.type.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * This class represents the Home page in Admin interface. This class
 * mostly provides navigation option to the admins from the activity itself, to
 * the four most important pages in the admin interface of the application i.e.
 * Search Doctors, Search Patients, Manage Wards, and Manage Global Documents.
 * Admin homepage also has a functionality to delete old data from database
 * once everyday automatically.
 */
public class AdminHome extends AdminMenuActivity {
    private ConstraintLayout m_AdminHomeGridParent;
    private LinearLayout m_LinearLayoutDoctors, m_LinearLayoutPatients, m_LinearLayoutWards, m_LinearLayoutDocuments;
    private Toolbar m_mainToolBar;
    private float m_pixeltodpConversion = Resources.getSystem().getDisplayMetrics().density;
    private int m_AdminHomeGridBoxMarginLeftRight, m_AdminHomeGridBoxMarginTopBottom;

    private Appointment m_apptObject = new Appointment(); //Appointment class object to use the appointment class method (makeDate).
    private FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();
    //UserAppointmentsPage class object to use the UserAppointmentsPage class methods.
    UserAppointmentsPage m_UserApptClassObject = new UserAppointmentsPage();

    /**/
    /*

    NAME

            onCreate - initializes AdminHome activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the AdminHome activity and links
            it to its respective layout resource file i.e. activity_admin_home
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24pm 01/30/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        //Class DeleteOldSchedules method only once per each day.
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String toDayDate = formatter.format(date);
        String lastVisitDate = preference.getString("mDateKey", "15/08/2021");

        if (toDayDate.equals(lastVisitDate)) {
            // this is same day visit again and again
        } else {
            // this is the  first time function is being accessed for the day
            preference.edit().putString("mDateKey", toDayDate);
            preference.edit().commit();
            DeleteOldSchedules();
        }

    }

    /**/
    /*

    NAME

            Doctors - navigates user to SearchDoctorsAdmin

    SYNOPSIS

            private void Doctors(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates admins to SearchDoctorsAdmin. This function is provided
            in the onClick attribute in xml file for the Doctors Grid in admin homepage. The
            function gets called when the Doctors view is clicked

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 07/26/2021

    */
    /**/
    public void Doctors(View a_view){
        startActivity(new Intent(getApplicationContext(), SearchDoctorsAdmin.class));
    }
    /**/
    /*

    NAME

            Users - navigates user to SearchUsersAdmin

    SYNOPSIS

            private void Users(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates admins to SearchUsersAdmin. This function is provided
            in the onClick attribute in xml file for the Users Grid in admin homepage. The function
            gets called when the Users view is clicked

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 07/26/2021

    */
    /**/
    public void Users(View a_view){
        startActivity(new Intent(getApplicationContext(), SearchUsersAdmin.class));
    }

    /**/
    /*

    NAME

            Wards - navigates user to ManageGroupAdmin

    SYNOPSIS

            private void Wards(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates admins to ManageGroupAdmin. This function is provided
            in the onClick attribute in xml file for the Wards Grid in homepage. The function
            gets called when the Wards view is clicked

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 07/26/2021

    */
    /**/
    public void Wards(View a_view){
        startActivity(new Intent(getApplicationContext(), ManageGroupAdmin.class));
    }

    /**/
    /*

    NAME

            Documents - navigates user to GlobalDocAdmin

    SYNOPSIS

            private void Documents(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates admins to GlobalDocAdmin. This function is provided
            in the onClick attribute in xml file for the Documents Grid in homepage. The function
            gets called when the Documents view is clicked

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 07/26/2021

    */
    /**/
    public void Documents(View a_view){
        startActivity(new Intent(getApplicationContext(), GlobalDocAdmin.class));
    }

    /**/
    /*

    NAME

            SetLeftRightMarginForGrids - sets the same left and right margin for the provided views

    SYNOPSIS

            private void SetLeftRightMarginForGrids(View a_view)
                a_view     --> view provided to set the margins for

    DESCRIPTION

            This function calls SetMargins function from UserAppointmentsPage class for the provided view.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30am 07/26/2021

    */
    /**/
    private void SetLeftRightMarginForGrids(View a_view){
        m_UserApptClassObject.SetMargins(a_view,
                                         m_AdminHomeGridBoxMarginLeftRight,
                                         m_AdminHomeGridBoxMarginTopBottom,
                                         m_AdminHomeGridBoxMarginLeftRight,
                                         m_AdminHomeGridBoxMarginTopBottom );
    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_admin_home.xml. Uses android method
            findViewById that, "finds a view that was identified by the android:id
           XML attribute that was processed in onCreate(Bundle)." Src: Android Documentation
           (https://developer.android.com/reference/android/app/Activity#findViewById(int))
           It also calls all other helper functions used in setting up the UI and getting
           contents for all the Views.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 01/30/2021

    */
    /**/
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_AdminHomeGridParent = findViewById(R.id.AdminHomeGridLayoutParent);
        m_LinearLayoutDoctors = findViewById(R.id.linearLayoutDoctors);
        m_LinearLayoutPatients = findViewById(R.id.LinearLayoutPatient);
        m_LinearLayoutWards = findViewById(R.id.linearLayoutWards);
        m_LinearLayoutDocuments = findViewById(R.id.linearLayoutDocuments);
        m_AdminHomeGridParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                m_AdminHomeGridParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);//width is ready
                m_AdminHomeGridBoxMarginLeftRight = (int) ((m_AdminHomeGridParent.getWidth() - 330*m_pixeltodpConversion))/2;
                m_AdminHomeGridBoxMarginTopBottom= (int) (15*m_pixeltodpConversion);
                SetLeftRightMarginForGrids(m_LinearLayoutDoctors);
                SetLeftRightMarginForGrids(m_LinearLayoutPatients);
                SetLeftRightMarginForGrids(m_LinearLayoutWards);
                SetLeftRightMarginForGrids(m_LinearLayoutDocuments);
            }
        });

    }


    /**/
    /*

    NAME

            DeleteOldSchedules - deletes a day old appointments data from Doctor's collection

    SYNOPSIS

            private void DeleteOldSchedules(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function deletes Doctor's past schedule from the database. This
            function is ran only once everyday the first time Admin Homepage is
            accessed. When the function runs, it deletes the previous day's
            appointment / schedules information from Doctor's collection. However,
            all the appointment information are still available in patient's collection

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 07/26/2021

    */
    /**/
    private void DeleteOldSchedules(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String YesterdaysDate = m_apptObject.MakeDateString(day, month + 1, year);

        CollectionReference db_DOC= m_fStore.collection("Doctors");

        //cleaning doctor's collection
        db_DOC.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        ArrayList<Map<String, Object>> YesterdaysAppointments = (ArrayList<Map<String, Object>>) document.get(YesterdaysDate);

                        if(YesterdaysAppointments!=null){
                            Map<String,Object> updates = new HashMap<>();
                            updates.put(YesterdaysDate, FieldValue.delete());
                            db_DOC.document(document.getId()).update(updates);
                        }
                    }
                }
            }
        });
    }


}