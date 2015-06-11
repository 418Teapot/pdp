package dk.http418.oconn;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONStringer;

/**
 * Created by zeb on 20-05-15.
 */
public class SetExtra extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {

        // vi skal hente json fra serveren!

        String veg = params[0];
        String amt = params[1];
        String phsh = "";

        System.out.println(veg+" opdateres med "+amt+" g extra!");

        veg = veg.replace(" ", "%20");
        JSONArray fromServer;
        if(params[2].equals("reset")) {
            fromServer = new JSONParser().getJSONFromUrl("http://178.62.139.101/pdp/setExtraVeggies.php?v=" + veg + "&amt=" + amt+"&r=y");;
        } else {
            fromServer = new JSONParser().getJSONFromUrl("http://178.62.139.101/pdp/setExtraVeggies.php?v=" + veg + "&amt=" + amt);
        }
        try {
            phsh = fromServer.get(0).toString();
        } catch (JSONException e){
            e.printStackTrace();
        }


        boolean rb = Boolean.parseBoolean(phsh);
        System.out.println("Veggie is: "+params[0]+" answer is: "+phsh+" parse is: "+rb+" "+params[2]);

        return(rb);
    }
}
