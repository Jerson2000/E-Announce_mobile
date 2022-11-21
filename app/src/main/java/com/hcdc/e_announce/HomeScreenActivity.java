package com.hcdc.e_announce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class HomeScreenActivity extends AppCompatActivity {

    private Context context = this;
    private DatePickerDialog datePickerDialog;
    private MaterialDatePicker materialDatePicker;
    private Button dateButton;
    private ImageButton backButton, chatBotButton;
    private TextView btnAllEvents;
    private Post post = new Post(context);
    private EventAdapter eventAdapter;
    private RecyclerView eventsView;
    private LinearLayoutManager manager;
    private Intent prevIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        prevIntent = getIntent();
        eventsView = findViewById(R.id.eventsView);
        dateButton = findViewById(R.id.datePickerButton);
        backButton = findViewById(R.id.backButton);
        chatBotButton = findViewById(R.id.chatBotButton);
        btnAllEvents = findViewById(R.id.btnAllEvents);
        manager = new LinearLayoutManager(context);
        eventsView.setLayoutManager(new GridLayoutManager(this,2));
//        initDatePicker();
        initMaterialDatePicker();
        dateButton.setText("All");
        chatBotButton.setOnClickListener(openChatBot);
        getAllEvents();

        btnAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateButton.setText("All");
                getAllEvents();
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                datePickerDialog.show();
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfile();
            }
        });

    }

    private void openProfile() {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
    }

//    private String getTodaysDate() {
//        Calendar cal = Calendar.getInstance();
//        int year = cal.get(Calendar.YEAR);
//        int month = cal.get(Calendar.MONTH);
//        month = month + 1;
//        int day = cal.get(Calendar.DAY_OF_MONTH);
//        return makeDateString(day, month, year);
//    }

    private void initMaterialDatePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("SELECT A DATE");

        materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Pair selectedDates = (Pair) selection;
                // then obtain the startDate & endDate from the range
                final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
                // assigned variables
                Date startDate = rangeDate.first;
                Date endDate = rangeDate.second;
                // Format the dates in ur desired display mode
                SimpleDateFormat simpleFormat = new SimpleDateFormat("MMMM dd, yyyy");
                dateButton.setText(simpleFormat.format(startDate) + " -\n" + simpleFormat.format(endDate));
                simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                getEvents(simpleFormat.format(startDate), simpleFormat.format(endDate));
            }
        });
    }

//    private void initDatePicker() {
//        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                month = month + 1;
//                getEvents(
////                        year +
////                        addZeroIfBelow10(month) +
////                        addZeroIfBelow10(day)
////                );
//                String date = makeDateString(day, month, year);
//                dateButton.setText(date);
//            }
//        };
//
//        Calendar cal = Calendar.getInstance();
//        int year = cal.get(Calendar.YEAR);
//        int month = cal.get(Calendar.MONTH);
//        int day = cal.get(Calendar.DAY_OF_MONTH);
//
//        int style = AlertDialog.THEME_HOLO_LIGHT;
//
//        datePickerDialog = new DatePickerDialog(
//                context, style, dateSetListener, year, month, day
//        );
////        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//    }

//    private String makeDateString(int day, int month, int year) {
//        return getMonthFormat(month) + " " + day + ", " + year;
//    }

    public String getMonthFormat(int month) {
        if (month == 1) return "January";
        if (month == 2) return "February";
        if (month == 3) return "March";
        if (month == 4) return "April";
        if (month == 5) return "May";
        if (month == 6) return "June";
        if (month == 7) return "July";
        if (month == 8) return "August";
        if (month == 9) return "September";
        if (month == 10) return "October";
        if (month == 11) return "November";
        if (month == 12) return "December";
        return String.valueOf(month);
    }

    View.OnClickListener openChatBot = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ChatBotActivity.class);
            intent.putExtras(prevIntent.getExtras());
            startActivity(intent);
        }
    };

    @Override
    public void onBackPressed() {
        // Empty so it does not go back to LoginActivity
    }

    public void getEvents(String fromDate, String toDate) {
        Map<String, String> params = new HashMap<>();
        params.put("from_date", fromDate);
        params.put("to_date", toDate);
        post.MYSQL("event.php", params, new VolleyCallBack() {
            @Override
            public void onSuccess(JSONArray result) {
                eventAdapter = new EventAdapter(context, result);
                eventsView.setAdapter(eventAdapter);
            }
        });
    }

    public void getAllEvents() {
        post.MYSQL("allEvent.php", new VolleyCallBack() {
            @Override
            public void onSuccess(JSONArray result) {
                eventAdapter = new EventAdapter(context, result);
                eventsView.setAdapter(eventAdapter);
            }
        });
    }

    // Also converts to string
//    public String addZeroIfBelow10(int number) {
//        if (number < 10)
//            return "0" + number;
//        return String.valueOf(number);
//    }
}