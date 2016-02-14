package com.nathansass.petal.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nathansass.petal.interfaces.GetUserCallback;
import com.nathansass.petal.R;
import com.nathansass.petal.data.ServerRequests;
import com.nathansass.petal.models.User;
import com.nathansass.petal.data.UserLocalStore;

// Possible should extended ActionBarActivity instead
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etUsername, etPassword;
    TextView tvRegisterLink;
    Button bLogin;

    UserLocalStore userLocalStore;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        etUsername     = (EditText) findViewById(R.id.etUsername);
        etPassword     = (EditText) findViewById(R.id.etPassword);
        bLogin         = (Button)   findViewById(R.id.bLogin);
        tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);

        bLogin.setOnClickListener(this);
        tvRegisterLink.setOnClickListener(this);

        userLocalStore = new UserLocalStore(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bLogin:

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                User user = new User(username, password);

                authenticate(user);

                break;
            case R.id.tvRegisterLink:

                startActivity(new Intent(this, RegisterActivity.class));

                break;
        }
    }

    private void authenticate(User user) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                }
            }
        });

    }

    private void logUserIn(User returnedUser){
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);

        /* Will change to another activity */
        startActivity(new Intent(this, MainActivity.class));
    }

    private void showErrorMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Incorrect User Details");
        builder.setPositiveButton("Ok", null);
        builder.show();
    }
}
