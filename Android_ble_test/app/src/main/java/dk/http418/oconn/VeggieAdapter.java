package dk.http418.oconn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class VeggieAdapter extends ArrayAdapter<Veggie> {

    private ArrayList<Veggie> veggies;
    public VeggieAdapter(Context context, int textViewResourceId, ArrayList<Veggie> objects) {
        super(context, textViewResourceId, objects);
        this.veggies = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        // vi konverterer et view.
        View v = convertView;

        if(v==null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.veggie_list, null);
        }

        Veggie i = veggies.get(position);

        if(i != null){
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView ttd = (TextView) v.findViewById(R.id.toptextdata);
            TextView mt = (TextView) v.findViewById(R.id.middletext);
            TextView mtd = (TextView) v.findViewById(R.id.middletextdata);
            ImageView iv = (ImageView) v.findViewById(R.id.veggie_image);

            if (tt != null){
                tt.setText("Navn: ");
            }
            if (ttd != null){
                ttd.setText(i.getName());
            }
            if (mt != null){
                mt.setText("Mængde: ");
            }
            if (mtd != null){
                mtd.setText(i.getAmount()+"g.");
            }

            if(iv != null){
                iv.setImageDrawable(i.getImgID());
            }
        }

        return v;

    }

}
