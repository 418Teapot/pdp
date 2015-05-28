package dk.http418.oconn;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONStringer;

/**
 * Created by zeb on 20-05-15.
 */
public class GetCheckout extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {

        // vi skal hente json fra serveren!

        String usr = params[0];
        String phsh = "";
        JSONArray fromServer = new JSONParser().getJSONFromUrl("http://178.62.139.101/pdp/checkoutBag.php?u="+usr);
        try {
            phsh = fromServer.get(0).toString();
        } catch (JSONException e){
            e.printStackTrace();
        }


        boolean rb = Boolean.parseBoolean(phsh);
        System.out.println("User is: "+params[0]+" String is: "+phsh+" parse is: "+rb);

        return(rb);
    }
}
