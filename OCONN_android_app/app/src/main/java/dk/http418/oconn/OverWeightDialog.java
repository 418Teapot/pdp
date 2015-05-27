package dk.http418.oconn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by zeb on 15-05-15.
 */
public class OverWeightDialog extends DialogFragment {

        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Du har taget for meget! Fjern lidt fra vægten for at fjerne dialogen!");

            return builder.create();
        }

}
