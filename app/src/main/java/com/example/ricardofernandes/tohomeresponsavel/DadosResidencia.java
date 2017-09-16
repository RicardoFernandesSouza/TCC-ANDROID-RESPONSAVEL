package com.example.ricardofernandes.tohomeresponsavel;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

/**
 * Created by RicardoFernandes on 25/04/2017.
 */

public class DadosResidencia extends ListActivity {
    String pid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // url to get all products list
    private static String url_all_products = "http://172.16.128.186//android_connect/get_residencia_dados.php";
  //  private static String url_all_products = "http://192.168.0.14//android_connect/get_residencia_dados.php";


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
   // private static final String TAG_PRODUCTS = "products";
    private static final String TAG_RESIDENCIA = "residencia";
    private static final String TAG_PID = "id";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_HOOD = "hood";
    private static final String TAG_ZIP = "zip_code";
    private static final String TAG_CITY = "city";
    private static final String TAG_STATE = "state";
    private static final String TAG_BEGIN = "begindate";
    private static final String TAG_END = "enddate";
    private static final String TAG_RESPID = "idresp";


    // products JSONArray
    JSONArray residencia = null;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_residencia);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = db.getUserDetailsResi();
        String userId = user.get("id");
        pid = userId;


        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllProducts().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
//                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
//                        .toString();
//
//                // Starting new intent
//                Intent in = new Intent(getApplicationContext(),
//                        EditProductActivity.class);
//                // sending pid to next activity
//                in.putExtra(TAG_PID, pid);
//
//                // starting new activity and expecting some response back
//                startActivityForResult(in, 100);
            }
        });

    }

    // Response from Edit Product Activity
    @Override
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

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DadosResidencia.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", pid));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    residencia = json.getJSONArray(TAG_RESIDENCIA);

                    // looping through All Products
                    for (int i = 0; i < residencia.length(); i++) {
                        JSONObject c = residencia.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String address = c.getString(TAG_ADDRESS);
                        String hood = c.getString(TAG_HOOD);
                        String zip_code = c.getString(TAG_ZIP);
                        String city = c.getString(TAG_CITY);
                        String state = c.getString(TAG_STATE);
                        String begindate = c.getString(TAG_BEGIN);
                        String enddate = c.getString(TAG_END);
                        String respId = c.getString(TAG_RESPID);


                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_ADDRESS, address);
                        map.put(TAG_HOOD, hood);
                        map.put(TAG_ZIP, zip_code);
                        map.put(TAG_CITY, city);
                        map.put(TAG_STATE, state);
                        map.put(TAG_BEGIN, begindate);
                        map.put(TAG_END, enddate);
                        map.put(TAG_RESPID, respId);

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

        /**
         * After completing background task Dismiss the progress dialog
         * **/
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
                            DadosResidencia.this, productsList,
                            R.layout.list_item, new String[] { TAG_PID,
                            TAG_ADDRESS,TAG_HOOD,TAG_ZIP,TAG_CITY,TAG_STATE,TAG_BEGIN,TAG_END},
                            new int[] { R.id.pid, R.id.address,R.id.hood,R.id.zip_code,R.id.city,R.id.state,R.id.begin,R.id.end});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }
}