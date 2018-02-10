package ivent.com.ivent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ivent.com.ivent.rest.ApiService;
import ivent.com.ivent.rest.RestClient;
import ivent.com.ivent.service.AuthenticationService;

public class MainActivity extends AppCompatActivity {

    protected static ApiService apiService = RestClient.getApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String loginToken = AuthenticationService.getAuthToken(getApplicationContext());
        if (loginToken.equals("")) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }


    }
}
