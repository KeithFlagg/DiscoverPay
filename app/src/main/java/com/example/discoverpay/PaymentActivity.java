package com.example.discoverpay;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

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

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {
        private ProgressBar spinner;
        private ProgressBar spinner2;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_payment);
            Button paymentButton = (Button) findViewById(R.id.paymentButton);
            Button paymentButton2 = (Button) findViewById(R.id.paymentButton2);
            ImageView imageView = (ImageView) findViewById(R.id.qrView);
            imageView.setVisibility(View.INVISIBLE);
            // init spinners
            spinner = (ProgressBar)findViewById(R.id.progressBar);
            spinner2 = (ProgressBar)findViewById(R.id.progressBar2);
            spinner2.setVisibility(View.INVISIBLE);
            spinner.isIndeterminate();
            spinner2.isIndeterminate();
            spinner.setVisibility(View.INVISIBLE);
            // initialize onClick
            paymentButton.setOnClickListener(this);
            paymentButton2.setOnClickListener(this);
            paymentButton.setClickable(false);
            // hide payment button on init
            paymentButton.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            Button paymentButton = (Button) findViewById(R.id.paymentButton);

            // Instantiate the RequestQueue for processing payments.
            RequestQueue queue = Volley.newRequestQueue(this);
            String paymentUrl = "http://35.247.105.231:8085/v1/api/ProcessPayment";

            // Instantiate the RequestQueue for qr generation.
            RequestQueue queue2 = Volley.newRequestQueue(this);
            String URL = "http://35.247.105.231:8085/v1/api/account/{account_id}/qrcode?accountId=2";

            // on click opens the new activity
            switch(v.getId()){
                // make a payment
                case R.id.paymentButton:
                    spinner2.setVisibility(View.VISIBLE);
                    JSONObject jsonBody = new JSONObject();
//                        jsonBody.put("", "");
                    final String requestBody = jsonBody.toString();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, paymentUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("VOLLEY", response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY: ", error.toString());
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

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString = "";
                            if (response != null) {
                                responseString = String.valueOf(response.statusCode);
                                // can get more details such as response.headers & response.data
                            }

                            // run ui manipulation on ui thread
                            new Handler(Looper.getMainLooper()).post(new Runnable(){
                                @Override
                                public void run() {
                                    spinner2.setVisibility(View.INVISIBLE);
                                    paymentButton.setClickable(true);
                                }
                            });
                            Log.i("API", "NetworkResponse: "+response.headers);
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };
                    // adds request to the request queue
                    queue.add(stringRequest);
                    int reqCode = 1;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    showNotification(this, "D-Pay", "A purchase has been made via D-Pay", intent, reqCode);
                    break;
                    // generate qr code
                case R.id.paymentButton2:
                    JSONObject jsonBody2 = new JSONObject();
//                        jsonBody.put("", "");
                    final String requestBody2 = jsonBody2.toString();
                    spinner.setVisibility(View.VISIBLE);

                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("VOLLEY", response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY: ", error.toString());
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return requestBody2 == null ? null : requestBody2.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody2, "utf-8");
                                return null;
                            }
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            ImageView imageView = (ImageView) findViewById(R.id.qrView);
                            String responseString = "";
                            if (response != null) {
                                responseString = String.valueOf(response.statusCode);
                                // can get more details such as response.headers & response.data
                            }

                            // convert resp to bitmap for image display
                            byte[] bytes = response.data;
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            // run ui manipulation on ui thread
                            new Handler(Looper.getMainLooper()).post(new Runnable(){
                                @Override
                                public void run() {
                                    spinner.setVisibility(View.INVISIBLE);
                                    // show qr
                                    imageView.setImageBitmap(bitmap);
                                    imageView.setVisibility(View.VISIBLE);
                                    // show make a payment option
                                    paymentButton.setClickable(true);
                                    paymentButton.setVisibility(View.VISIBLE);
                                }
                            });

                            Log.i("API", "NetworkResponse: "+response.headers);
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };
                    // adds request to the request queue
                    queue2.add(stringRequest2);
                default:break;
            }
        }

        @Override
        public void onBackPressed() {
        }

        public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_IMMUTABLE);
            String CHANNEL_ID = "d_pay";// The id of the channel.

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.discover_name_2_round)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "D-Pay";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
            Log.d("showNotification", "showNotification: " + reqCode);
        }
}
