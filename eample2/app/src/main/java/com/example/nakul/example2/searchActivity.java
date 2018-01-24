package com.example.nakul.example2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class searchActivity extends AppCompatActivity {

        private String TAG = searchActivity.class.getSimpleName();

        private ProgressDialog pDialog;
        private ListView lv;

        // URL to get contacts JSON
        private static String url = "http://52.168.64.79:3000/customer/";
        private String original_url = url;
        ArrayList<HashMap<String, String>> contactList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search);


        Button submit = (Button) findViewById(R.id.Submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                contactList = new ArrayList<>();

                lv = (ListView) findViewById(R.id.list);

                EditText mEdit   = (EditText)findViewById(R.id.et_search_box);

                String x = mEdit.getText().toString();

                x=x.toLowerCase();

                url=original_url+x;
                Log.d("onClick URL: ",original_url);
                new GetContacts().execute();
            }
        });


        }

        /**
         * Async task class to get json by making HTTP call
         */
        private class GetContacts extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // Showing progress dialog
                pDialog = new ProgressDialog(searchActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();

            }

            @Override
            protected Void doInBackground(Void... arg0) {
                HttpHandler sh = new HttpHandler();

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url);

                Log.e(TAG, "Response from url: " + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        JSONArray contacts = jsonObj.getJSONArray("rows");

                        // looping through All Contacts
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);

                            String name = c.getString("nam");

                            String met1 = c.getString("met1");
                            String met2 = c.getString("met2");
                            String met3 = c.getString("met3");
                            String met4 = c.getString("met4");
                            // Phone node is JSON Object



                            // tmp hash map for single contact
                            HashMap<String, String> contact = new HashMap<>();

                            // adding each child node to HashMap key => value
//                            contact.put("rating", rating);
//                            contact.put("nam", name);
//                            contact.put("category", category);
//                            contact.put("sentiment", sentiment);
                            contact.put("nam",name);
                            contact.put("met1",met1);
                            contact.put("met2",met2);
                            contact.put("met3",met3);
                            contact.put("met4",met4);


                            // adding contact to contact list
                            contactList.add(contact);
                        }
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                    }
                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // Dismiss the progress dialog
                if (pDialog.isShowing())
                    pDialog.dismiss();
                /**
                 * Updating parsed JSON data into ListView
                 * */
                ListAdapter adapter = new SimpleAdapter(
                        searchActivity.this, contactList,
                        R.layout.list_item, new String[]{"nam", "met1","met2","met3","met4"
                        }, new int[]{R.id.name,
                        R.id.met1, R.id.met2,R.id.met3,R.id.met4});

                lv.setAdapter(adapter);
            }

        }
    }
