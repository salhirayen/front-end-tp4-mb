package com.dsi3.isetkb.tp4application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity {

    EditText editName, editPassword;
    Button btnLogin, btnCancel, btnGoToRegister;
    TextView tvMsg;

    String URL = "http://10.94.19.87/tp4/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCancel = findViewById(R.id.btnCancel);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        tvMsg = findViewById(R.id.tvMsg);

        btnLogin.setOnClickListener(v -> {
            String username = editName.getText().toString().trim();
            String password = editPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                tvMsg.setText("Veuillez remplir tous les champs");
            } else {
                loginWithVolley(username, password);
            }
        });

        btnCancel.setOnClickListener(v -> {
            editName.setText("");
            editPassword.setText("");
            tvMsg.setText("");
        });

        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginWithVolley(String username, String password) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("VOLLEY_LOGIN", response);
                            try {
                                JSONObject json = new JSONObject(response);
                                int success = json.getInt("success");

                                if (success == 1) {
                                    Intent intent = new Intent(MainActivity.this, AcceuilAct.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    tvMsg.setText("Username ou password incorrect");
                                }
                            } catch (Exception e) {
                                tvMsg.setText("Réponse serveur invalide");
                                Log.e("VOLLEY_LOGIN", "JSON error", e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tvMsg.setText("Erreur de connexion au serveur");
                            Log.e("VOLLEY_LOGIN", error.toString());
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
            tvMsg.setText("Erreur lors de la requête");
        }
    }
}
