package dk.http418.oconn;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends Activity {

    public static final String PREFS_NAME = "oconnSettings";
    public static final String LOGON_STR = "loggedOn";

    public static boolean loggedOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // check om der er sat et login!
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        loggedOn = settings.getBoolean(LOGON_STR, false);

        System.out.println("Loggedon? "+loggedOn);

        if(loggedOn){
            System.out.println("Vi er allerede logget på!");
            Intent intent = new Intent(getApplicationContext(), ScanWeight.class);
            startActivity(intent);
            finish();
        }

        final TextView usr = (TextView) findViewById(R.id.brugernavn);
        final TextView pw = (TextView) findViewById(R.id.password);

        if(usr != null) {
            usr.requestFocus();
        }
        Button loginBtn = (Button) findViewById(R.id.login_knap);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // når vi klikker - hent usr og pw!
                if (usr != null && pw != null) {
                    String usrname = usr.getText().toString();
                    String passw = pw.getText().toString();

                    // salt og digest vores string
                    String salt = digestString("abcdef");
                    final String passMD5 = digestString(salt + passw + salt);

                    // check med serveren
                    new GetLogin() {

                        @Override
                        public void onPostExecute(String s) {
                            if (s.equals(passMD5)) {
                                SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
                                SharedPreferences.Editor edit = settings.edit();
                                edit.putBoolean(LOGON_STR, true);
                                edit.commit();
                                System.out.println("LOGGED IN!");
                                Intent intent = new Intent(getApplicationContext(), ScanWeight.class);
                                startActivity(intent);
                                finish();

                            } else {
                                System.out.println("ACCESS DENIED MOTHERFUCKER!");
                            }
                        }

                    }.execute(usrname);

                }

            }
        });

    }

    private String digestString(String s){

        String rs = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(s.getBytes());
            byte byteData[] = md5.digest();

            StringBuffer hexString = new StringBuffer();
            for(int i=0; i<byteData.length;i++){
                String hex = Integer.toHexString(0xff & byteData[i]);
                if(hex.length()==1) hexString.append('0');
                hexString.append(hex);
            }

            rs =  hexString.toString();
            //System.out.println("HEX: "+ hexString.toString());


        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return rs;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_selveggie, menu);
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
}
