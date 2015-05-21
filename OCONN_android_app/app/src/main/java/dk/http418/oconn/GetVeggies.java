package dk.http418.oconn;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GetVeggies extends AsyncTask<String, Void, ArrayList<Veggie>>{

    protected ArrayList<Veggie> doInBackground(String... params){

        ArrayList<Veggie> veggies = new ArrayList<Veggie>();
        JSONArray jarr = new JSONParser().getJSONFromUrl("http://178.62.139.101/pdp/getVeggies.php");

        if(jarr != null){
            for(int i = 0; i < jarr.length(); i++){
                try {
                    JSONObject tmpArr = (JSONObject) jarr.get(i);
                    String name = tmpArr.getString("veggie");
                    int amt = tmpArr.getInt("amount");
                    String date = tmpArr.getString("date");

                    Veggie v = new Veggie(date, name, amt);
                    veggies.add(v);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }

        return veggies;
    }
}