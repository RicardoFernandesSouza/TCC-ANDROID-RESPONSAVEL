package com.example.ricardofernandes.tohomeresponsavel;

/**
 * Created by RicardoFernandes on 21/05/2017.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ricardofernandes.tohomeresponsavel.helper.SQLiteHandler;
import com.example.ricardofernandes.tohomeresponsavel.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static java.util.Calendar.getInstance;

public class MarcaVisita extends ListActivity {
    String pid;

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    JSONParser jParser = new JSONParser();


    ArrayList<HashMap<String, String>> productsList;

    // url to create new product
    private static String url_get_data = "http://172.16.128.186//android_connect/get_data_visita.php";
    private static String url_create_product = "http://172.16.128.186//android_connect/create_product.php";

    private static final String TAG_VISITA = "visita";
    private static final String TAG_PID = "pid";
    private static final String TAG_DATA = "datav";
    private static final String TAG_IDRESI = "idresi";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    JSONArray visita = null;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marca_visita);
        setTitle("Marcar Visita");

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetailsResi();
        String userId = user.get("id");
        pid = userId;
        productsList = new ArrayList<HashMap<String, String>>();
        new LoadAllProducts().execute();
        ListView lv = getListView();

        final Calendar cal = getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        showDialogOnButtonClick();

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MarcaVisita.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("idresi", pid));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_data, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    visita = json.getJSONArray(TAG_VISITA);

                    // looping through All Products
                    for (int i = 0; i < visita.length(); i++) {
                        JSONObject c = visita.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String data = c.getString(TAG_DATA);
                        String idresi = c.getString(TAG_IDRESI);


                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_DATA, data);
                        map.put(TAG_IDRESI, idresi);

                        // adding HashList to ArrayList
                        productsList.add(map);
                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
//                    Intent i = new Intent(getApplicationContext(),
//                            NewProductActivity.class);
//                    // Closing all previous activities
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            MarcaVisita.this, productsList,
                            R.layout.list_item_data, new String[] { TAG_PID,
                            TAG_DATA,TAG_IDRESI},
                            new int[] { R.id.pid, R.id.data,R.id.idresi});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }
    }

        Button mbtn;
        int year;

        public int getMonth() {
            return month + 1;
        }

        public int getDay() {
            return day;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        public TimePickerDialog.OnTimeSetListener getTpickerListener() {
            return tpickerListener;
        }

        public DatePickerDialog.OnDateSetListener getDpickerListener() {
            return dpickerListener;
        }

        int month;
        int day;
        int hour, minute;

        public void showDialogOnButtonClick() {

            mbtn = (Button) findViewById(R.id.ibtn1);

            mbtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog(0);
                        }
                    }
            );

        }

        public void showAlert() {
            AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
            myAlert.setMessage("Informe a Hora")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showDialog(1);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i1) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setTitle("Hora")
                    .create();
            myAlert.show();

        }

        protected Dialog onCreateDialog(int id) {
            if (id == 0) {
                return new DatePickerDialog(this, dpickerListener, year, month, day);
            }
            if (id == 1) {
                return new TimePickerDialog(this, tpickerListener, hour, minute, false);
            }

            return null;
        }

        private DatePickerDialog.OnDateSetListener dpickerListener
                = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1;
                day = i2;
                // Toast.makeText(MarcaVisita.this,year+"/"+month+"/"+day, Toast.LENGTH_SHORT).show();
                showAlert();

            }
        };
        private TimePickerDialog.OnTimeSetListener tpickerListener
                = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                hour = h;
                minute = m;
                if (minute < 10) {
                    // Toast.makeText(MarcaVisita.this, hour + ":" + "0"+minute, Toast.LENGTH_SHORT).show();
                    sendEmail();

                    // showAlert();
                } else
                    // Toast.makeText(MarcaVisita.this, hour + ":" + minute, Toast.LENGTH_SHORT).show();
                    sendEmail();

                // showAlert();

            }
        };


        protected void sendEmail() {
            Log.i("Send email", "");
            //  int dia,mes,ano,hora,minuto;
            String[] TO = {"ricardo_fernandes.souza@hotmail.com"};
            String[] CC = {"recardenho@gmail.com"};
            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Visita na Obra");
            if (getMinute() < 10) {
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Olá, vou visitar a obra na data: " + getDay() + "/" + getMonth() + "/" + getYear() + "\nNo horário das: " + getHour() + ":" + "0" + getMinute() + "\nEsse é um e-mail automático do App ToHome");
                new CreateNewProduct().execute();

            } else {
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Olá, vou visitar a obra na data: " + getDay() + "/" + getMonth() + "/" + getYear() + "\nNo horário das: " + getHour() + ":" + getMinute() + "\nEsse é um e-mail automático do App ToHome");
                new CreateNewProduct().execute();

            }
            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                finish();
                Log.i("Finished send email...", "");
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MarcaVisita.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
            finish();
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

    class CreateNewProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MarcaVisita.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String data;
            if(getMinute()<10) {
                data = "Visita na Obra: " + getDay() + "/" + getMonth() + "/" + getYear() + "\n" + "Horario: "+ getHour()+":"+"0"+getMinute() ;
                data.toString();
            }
            else{
                data = "Visita na Obra: " + getDay() + "/" + getMonth() + "/" + getYear() + "\n" + "Horario: "+ getHour()+":"+getMinute();
                data.toString();
            }

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("datav", data));
            params.add(new BasicNameValuePair("idresi", pid));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                   // Intent i = new Intent(getApplicationContext(), MarcaVisita.class);
                  //  startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

    }


