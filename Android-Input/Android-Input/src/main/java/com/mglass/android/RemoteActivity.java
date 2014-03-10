package com.mglass.android;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;
import android.widget.Toast;

public class RemoteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.activity_remote_container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.remote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_remote, container, false);

            Button btnReturn = (Button)  rootView.findViewById(R.id.btnReturn);
            Button btnLeft = (Button) rootView.findViewById(R.id.btnLeft);
            Button btnRight = (Button) rootView.findViewById(R.id.btnRight);
            Button btnSelect = (Button) rootView.findViewById(R.id.btnSelect);
            Button btnBack = (Button) rootView.findViewById(R.id.btnBack);

            if(btnReturn != null){
                btnReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        getActivity().finish();
                    }
                });
            }
            if(btnLeft != null){
                btnLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "Sending 'Left' Swipe to Glass!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if(btnRight != null){
                btnRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "Sending 'Right' Swipe to Glass!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if(btnSelect != null){
                btnSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "Sending 'Select' Input to Glass!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if(btnBack != null){
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "Sending 'Back' Gesture to Glass!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return rootView;
        }
    }

}
