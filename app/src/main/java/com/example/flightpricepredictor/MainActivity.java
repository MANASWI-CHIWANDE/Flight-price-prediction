package com.example.flightpricepredictor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AnalogClock;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView departureDisplayDate;
    private TextView arrivalDisplayDate;
    private TextView departureTime;
    private TextView arrivalTime;
    private TextView stopsLabel;
    private EditText stopsEditText;
    private DatePickerDialog.OnDateSetListener departureDateSetListener;
    private DatePickerDialog.OnDateSetListener arrivalDateSetListener;
    Button predict;
    TextView result;
    String url = "https://manaswicflightforesight.onrender.com/predict";

    private String desdata,arrdata, airlinedata , selectedDepartureTime,selectedDepartureDate , selectedArrivalTime,selectedArrivalDate;

    Spinner spinner_airlines;
    Spinner spinner_source;
    Spinner spinner_destination;
    String airlinesList[] = {"Select Airlines","Jet_Airways", "IndiGo","Air_India", "Multiple_carriers", "SpiceJet", "Vistara" , "Multiple_carriers_Premium_economy","Jet_Airways_Business","Vistara_Premium_economy", "Trujet"};
    String sourceList[]={"Select Source","Chennai","Delhi","Kolkata","Mumbai"};
    String destinationList[]={"Select Destination","Cochin","Delhi","Hyderabad","Kolkata","NewDelhi"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        departureDisplayDate = findViewById(R.id.departure_date);
        arrivalDisplayDate = findViewById(R.id.arrival_date);
        departureTime = findViewById(R.id.departure_time);
        arrivalTime = findViewById(R.id.arrival_time);
        predict=findViewById(R.id.predict_button);
        result=findViewById(R.id.prediction_text);

        departureDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(departureDateSetListener);
            }
        });

        arrivalDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(arrivalDateSetListener);
            }
        });

        departureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(departureTime);
            }
        });
        arrivalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(arrivalTime);
            }
        });

        departureDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                selectedDepartureDate = year+"-"+month+"-"+dayOfMonth;
                departureDisplayDate.setText(date);
            }
        };

        arrivalDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                selectedArrivalDate = year+"-"+month+"-"+dayOfMonth;
                arrivalDisplayDate.setText(date);
            }
        };

        //for stops
        stopsLabel=findViewById(R.id.stops_label);
        stopsEditText=findViewById(R.id.stops_edittext);
        String stops = stopsEditText.getText().toString();
        //for airlines
        spinner_airlines=findViewById(R.id.airline_spinner);
        ArrayAdapter<String>  adapter_Airlines = new ArrayAdapter<String>(MainActivity.this, R.layout.for_spinner,airlinesList);
        adapter_Airlines.setDropDownViewResource(R.layout.for_spinner );
        spinner_airlines.setAdapter(adapter_Airlines);
        spinner_airlines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String valueAirlines = parent.getItemAtPosition(position).toString();
                airlinedata = valueAirlines;
                Toast.makeText(MainActivity.this, valueAirlines, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //for source
        spinner_source=findViewById(R.id.source_spinner);
        ArrayAdapter<String>  adapter_Source = new ArrayAdapter<String>(MainActivity.this, R.layout.for_spinner,sourceList);
        adapter_Source.setDropDownViewResource(R.layout.for_spinner );
        spinner_source.setAdapter(adapter_Source);
        spinner_source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String valueSource = parent.getItemAtPosition(position).toString();
                arrdata = valueSource;
                Toast.makeText(MainActivity.this, valueSource, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //for destination
        spinner_destination=findViewById(R.id.destination_spinner);
        ArrayAdapter<String>  adapter_destination = new ArrayAdapter<String>(MainActivity.this, R.layout.for_spinner,destinationList);
        adapter_Airlines.setDropDownViewResource(R.layout.for_spinner );
        spinner_destination.setAdapter(adapter_destination);
        spinner_destination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String valueDestination = parent.getItemAtPosition(position).toString();
                desdata=valueDestination;
                Toast.makeText(MainActivity.this, valueDestination, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
            // Get the input values from the user
            String stops = stopsEditText.getText().toString();

            // Perform input validation
            if (selectedDepartureDate == null || selectedDepartureDate.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please select Departure Date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedDepartureTime == null || selectedDepartureTime.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please select Departure Time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedArrivalDate == null || selectedArrivalDate.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please select Arrival Date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedArrivalTime == null || selectedArrivalTime.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please select Arrival Time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (stops.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter the number of stops", Toast.LENGTH_SHORT).show();
                return;
            }

            if (airlinedata.equals("Select Airlines")) {
                Toast.makeText(MainActivity.this, "Please select an airline", Toast.LENGTH_SHORT).show();
                return;
            }

            if (arrdata.equals("Select Source")) {
                Toast.makeText(MainActivity.this, "Please select a source", Toast.LENGTH_SHORT).show();
                return;
            }

            if (desdata.equals("Select Destination")) {
                Toast.makeText(MainActivity.this, "Please select a destination", Toast.LENGTH_SHORT).show();
                return;
            }
                //hit the api -> Volley
                StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject= new JSONObject(response);
                                    String data = jsonObject.getString("prediction");
                                    result.setText("Estimated Cost for your journey is : Rs." + data);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                ){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> parameters = new HashMap<>();
                        //parameters.put();
                        parameters.put("Dep_Time",selectedDepartureDate+"T"+selectedDepartureTime);
                        parameters.put("Arrival_Time",selectedArrivalDate+"T"+selectedArrivalTime);
                        parameters.put("stops",stops);
                        parameters.put("airline",airlinedata);
                        parameters.put("Source",arrdata);
                        parameters.put("Destination",desdata);
                        return parameters;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(stringRequest);

            }
        });

    }

    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    private void showTimePickerDialog(final TextView textView) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        textView.setText(time);

                        if(textView==departureTime){
                            selectedDepartureTime = time;
                        }
                        else if(textView==arrivalTime){
                            selectedArrivalTime = time;
                        }
                        // Retrieve hour and minute separately
                        int selectedHour = hourOfDay;
                        int selectedMinute = minute;

                        // You can use the selected hour and minute as needed
                        // For example, perform calculations or store them in variables
                    }
                }, hour, minute, true);
        dialog.show();
    }
}

