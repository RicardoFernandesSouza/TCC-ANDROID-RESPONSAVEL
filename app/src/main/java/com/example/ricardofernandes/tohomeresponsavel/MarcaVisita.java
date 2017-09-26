package com.example.ricardofernandes.tohomeresponsavel;

/**
 * Created by RicardoFernandes on 21/05/2017.
 */

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.ricardofernandes.tohomeresponsavel.helper.SQLiteHandler;
import com.example.ricardofernandes.tohomeresponsavel.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarcaVisita extends ListActivity {
    String pid;
    String visitaId;

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    JSONParser jParser = new JSONParser();


    ArrayList<HashMap<String, String>> productsList;

    // url to create new product
    private static String url_get_data = "http://172.16.128.186//android_connect/get_data_visita.php";
    private static String update_data_confirma = "http://172.16.128.186//android_connect/update_data_confirma.php";
    private static String update_data_nega = "http://172.16.128.186//android_connect/update_data_nega.php";


    private static final String TAG_VISITA = "visita";
    private static final String TAG_PID = "pid";
    private static final String TAG_DATA = "datav";
    private static final String TAG_IDRESI = "idresi";
    private static final String TAG_STATUS = "status";

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
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String idVisita = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();
                String idResi = ((TextView) view.findViewById(R.id.idresi)).getText()
                        .toString();
                visitaId = idVisita;

                showAlert();

            }
        });



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
            pDialog.setMessage("Carregando Visitas...");
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
                        String status  = c.getString(TAG_STATUS);


                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_DATA, data);
                        map.put(TAG_IDRESI, idresi);
                        map.put(TAG_STATUS, status);

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
                            TAG_DATA,TAG_IDRESI,TAG_STATUS},
                            new int[] { R.id.pid, R.id.data,R.id.idresi,R.id.status});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }
    }

    public void showAlert() {
        AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
        myAlert.setMessage("Confirmar presença nessa data?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new UpdateVisitaConfirma().execute();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i1) {
                        new UpdateVisitaNega().execute();
                    }
                })
                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        myAlert.show();

    }

    class UpdateVisitaConfirma extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MarcaVisita.this);
            pDialog.setMessage("Negando Visita...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", visitaId));
            // params.add(new BasicNameValuePair("idresi", idResi));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(update_data_confirma,
                    "POST", params);

            // check log cat fro response
            Log.d("Update Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), MarcaVisita.class);
                    startActivity(i);

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

    class UpdateVisitaNega extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MarcaVisita.this);
            pDialog.setMessage("Negando Visita...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", visitaId));
            // params.add(new BasicNameValuePair("idresi", idResi));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(update_data_nega,
                    "POST", params);

            // check log cat fro response
            Log.d("Update Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), MarcaVisita.class);
                    startActivity(i);

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


