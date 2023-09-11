package com.ucn.loggin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class SessionFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    RequestQueue requestQueue;
    JsonRequest jsonRequest;
    EditText userText, pwdText;
    ImageButton btnConsultar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session, container, false);
        userText = (EditText) view.findViewById(R.id.inputTextUser);
        pwdText = (EditText) view.findViewById(R.id.inputPwd);
        btnConsultar = (ImageButton) view.findViewById(R.id.consultar);


        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certInputStream = getResources().openRawResource(R.raw.certi);
            Certificate ca;
            try {
                ca = cf.generateCertificate(certInputStream);
            } finally {
                certInputStream.close();
            }

            // Añade el certificado al almacén de claves
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("mi_certificado", ca);

            // Crea un administrador de confianza con el certificado
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Crea una conexión SSL personalizada que confíe en el certificado
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
          //  requestQueue = Volley.newRequestQueue(getContext());

            // Configura Volley para usar la conexión SSL personalizada
             requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext(), new HurlStack(null, sslContext.getSocketFactory()));
            // ... Continúa con tus solicitudes de Volley

        } catch (Exception e) {
            e.printStackTrace();
        }

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        return view;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),"No se encontró el usuario "+userText.getText() + error.toString() ,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(getContext(), "Usuario encontrado " + userText.getText().toString(), Toast.LENGTH_SHORT).show();
        User user = new User();
        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject= null;
        try {
            jsonObject = jsonArray.getJSONObject(0);
            user.setUser(jsonObject.optString("user"));
            user.setPwd(jsonObject.optString("pwd"));
            user.setNames(jsonObject.optString("names"));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        intent.putExtra(WelcomeActivity.names, user.getNames());
        startActivity(intent);
    }

    private void login(){


        String url = "https://desarrolloappmobile.000webhostapp.com/sesion.php?user="
                +userText.getText().toString()
                +"&pwd="
                +pwdText.getText().toString();
        jsonRequest = new JsonObjectRequest(Request.Method.GET, url,null, this, this);
        requestQueue.add(jsonRequest);

    }
}