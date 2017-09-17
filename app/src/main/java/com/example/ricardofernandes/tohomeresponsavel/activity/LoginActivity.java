package com.example.ricardofernandes.tohomeresponsavel.activity;

/**
 * Created by RicardoFernandes on 03/06/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ricardofernandes.tohomeresponsavel.EscolhaProjeto;
import com.example.ricardofernandes.tohomeresponsavel.R;
import com.example.ricardofernandes.tohomeresponsavel.app.AppConfig;
import com.example.ricardofernandes.tohomeresponsavel.app.AppController;
import com.example.ricardofernandes.tohomeresponsavel.helper.SQLiteHandler;
import com.example.ricardofernandes.tohomeresponsavel.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {
    Intent pid;

    private static final String TAG = AppController.class.getSimpleName();
    private Button btnLogin;
   // private Button btnLinkToRegister;
    private EditText inputUsername;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private static final String TAG_PID = "pid";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();

        // getting product id (pid) from intent
        i.putExtra(TAG_PID, pid);

       // setTheme(R.style.AppTheme_NoActionBar);

        setContentView(R.layout.activity_login);

        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
       // btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, EscolhaProjeto.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String username = inputUsername.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!username.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(username, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Preencha todos os campos!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
//        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),
//                        RegisterActivity.class);
//               startActivity(i);
//                finish();
//            }
//        });

    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String username, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite


                        JSONObject user = jObj.getJSONObject("resp");
                        String id = user.getString("id");
                        String name = user.getString("name");
                        String username = user.getString("username");
                        String password = user.getString("password");
                        String email = user.getString("email");

//                        JSONObject userresp = jObj.getJSONObject("resp");
//                        String idresp = userresp.getString("id");
//                        String nameresp = userresp.getString("name");
//                        String usernameresp = userresp.getString("username");
//                        String passwordresp = userresp.getString("password");
//                        String emailresp = userresp.getString("email");
//
//                        JSONObject userresi = jObj.getJSONObject("residencia");
//                        String idresi = userresi.getString("id");
//                        String address = userresi.getString("address");
//                        String hood = userresi.getString("hood");
//                        String city = userresi.getString("city");
//                        String state = userresi.getString("state");
//                        String idresicliente = userresi.getString("idcliente");
//                        String idresiresp = userresi.getString("idresp");

//                        String email = user
//                                .getString("email");

                        // Inserting row in users table
                        db.addUser(id,name,username,password,email);
                    //    db.addResp(idresp,nameresp,usernameresp,passwordresp,emailresp);
                    //    db.addResidencia(idresi,address,hood,city,state,idresicliente,idresiresp);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                EscolhaProjeto.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
