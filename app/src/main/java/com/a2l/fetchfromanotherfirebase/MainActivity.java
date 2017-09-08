package com.a2l.fetchfromanotherfirebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

//import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView allusers;
    ProgressDialog mProgressDialog;
    FirebaseDatabase firebaseDatabase;
    ListingAdapter adapter;
    ArrayList<User> users = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Firebase.setAndroidContext(this);
        allusers = (ListView) findViewById(R.id.allusers);
        adapter = new ListingAdapter(MainActivity.this, users);
        allusers.setAdapter(adapter);
        getDataFromServer();
    }

    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getDataFromServer() {
        showProgressDialog();
        firebaseDatabase.getReference(Config.FIREBASE_URL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        User user = postSnapShot.getValue(User.class);
                        users.add(user);
                        adapter.notifyDataSetChanged();
                    }
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private class ListingAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<User> users;

        public ListingAdapter(Context con, ArrayList<User> users) {
            context = con;
            layoutInflater = LayoutInflater.from(context);
            this.users = users;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.adapter_listing, null, false);
                holder = new ViewHolder();
                holder.message = (TextView) convertView.findViewById(R.id.ListMessage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            User user = users.get(position);
            holder.message.setText(user.getMessage());
            return convertView;
        }

        public class ViewHolder {
            public TextView message;
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}

