package com.dsi3.isetkb.tp4application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RegisterActivity extends Activity {

    EditText editUsername, editPassword, editNom, editPrenom, editEmail, editTelephone;
    Button btnRegister, btnBack;
    TextView tvRegisterMsg;

    String URL = "http://10.94.19.87/tp4/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editNom = findViewById(R.id.editNom);
        editPrenom = findViewById(R.id.editPrenom);
        editEmail = findViewById(R.id.editEmail);
        editTelephone = findViewById(R.id.editTelephone);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        tvRegisterMsg = findViewById(R.id.tvRegisterMsg);

        btnRegister.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString();
            String nom = editNom.getText().toString().trim();
            String prenom = editPrenom.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String telephone = editTelephone.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
                tvRegisterMsg.setText("Veuillez remplir tous les champs");
            } else {
                registerUserWithVolley(username, password, nom, prenom, email, telephone);
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUserWithVolley(String username, String password, String nom, String prenom, String email, String telephone) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("action", "register");
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            jsonBody.put("nom", nom);
            jsonBody.put("prenom", prenom);
            jsonBody.put("email", email);
            jsonBody.put("telephone", telephone);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("VOLLEY_REGISTER", response);
                            try {
                                JSONObject json = new JSONObject(response);
                                int success = json.getInt("success");
                                String message = json.getString("message");
                                if (success == 1) {
                                    tvRegisterMsg.setText("Inscription réussie ! Redirection...");
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    tvRegisterMsg.setText("Erreur : " + message);
                                }
                            } catch (Exception e) {
                                tvRegisterMsg.setText("Erreur de parsing de la réponse serveur");
                                Log.e("VOLLEY_REGISTER", "JSON error", e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tvRegisterMsg.setText("Impossible de contacter le serveur");
                            Log.e("VOLLEY_REGISTER", error.toString());
                        }
                    }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            tvRegisterMsg.setText("Erreur lors de la requête");
        }
    }
}
