package com.example.ricardofernandes.tohomeresponsavel;

/**
 * Created by RicardoFernandes on 07/06/2017.
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

public class EtapasResponsavel extends ListActivity {
    String idResi;
    String EtapaId;

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    JSONParser jParser = new JSONParser();


    ArrayList<HashMap<String, String>> productsList;

    private static String url_get_etapas = "http://172.16.128.186//android_connect/get_etapas_resp.php";
    private static String url_update_etapa = "http://172.16.128.186//android_connect/update_etapa_resp.php";
//    private static String url_get_etapas = "http://192.168.0.14//android_connect/get_etapas.php";
    //  private static String url_update_etapa = "http://192.168.0.14//android_connect/update_etapa.php";

    private static final String TAG_ETAPA = "tbl_name";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_DETAILS = "details";
    private static final String TAG_STATUS = "status";
    private static final String TAG_IDRESI = "id_residencia";

    private static final String TAG_SUCCESS = "success";

    JSONArray etapas = null;

    private SQLiteHandler db;
    private SessionManager session;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etapas_responsavel);
        setTitle("Etapas Responsável");
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetailsResi();
        String ResiId = user.get("id");
        idResi = ResiId;



        productsList = new ArrayList<HashMap<String, String>>();
        new LoadAllProducts().execute();
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String idEtapa = ((TextView) view.findViewById(R.id.id)).getText()
                        .toString();
                String idResi = ((TextView) view.findViewById(R.id.idresi)).getText()
                        .toString();
                EtapaId = idEtapa;
                //   db.addEtapa(idEtapa,idResi,null,null,null);
                // Starting new intent
//                Intent in = new Intent(getApplicationContext(),
//                        MainActivity.class);
                showAlert();
                // sending pid to next activity
                //db.deleteEtapas();


                // starting new activity and expecting some response back
                //  startActivityForResult(in, 100);
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
            pDialog = new ProgressDialog(EtapasResponsavel.this);
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
            params.add(new BasicNameValuePair("id_residencia", idResi));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_etapas, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    etapas = json.getJSONArray(TAG_ETAPA);

                    // looping through All Products
                    for (int i = 0; i < etapas.length(); i++) {
                        JSONObject c = etapas.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        String details = c.getString(TAG_DETAILS);
                        String idresi = c.getString(TAG_IDRESI);
                        String status = c.getString(TAG_STATUS);


                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_DETAILS, details);
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
                            EtapasResponsavel.this, productsList,
                            R.layout.list_item_etapa_resp, new String[] { TAG_PID,
                            TAG_NAME,TAG_DETAILS,TAG_STATUS,TAG_IDRESI},
                            new int[] { R.id.id, R.id.name,R.id.details,R.id.status,R.id.idresi});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }
    }

    class UpdateEtapa extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EtapasResponsavel.this);
            pDialog.setMessage("Updating Product..");
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
            params.add(new BasicNameValuePair("id", EtapaId));
            // params.add(new BasicNameValuePair("idresi", idResi));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_etapa,
                    "POST", params);

            // check log cat fro response
            Log.d("Update Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), EtapasResponsavel.class);
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
    public void showAlert() {
        AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
        myAlert.setMessage("Deseja Concluir esta etapa?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new UpdateEtapa().execute();
                        //  db.deleteEtapas();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i1) {
                        dialogInterface.dismiss();
                    }
                })
                .setTitle("Etapa concluída")
                .create();
        myAlert.show();

    }




}

