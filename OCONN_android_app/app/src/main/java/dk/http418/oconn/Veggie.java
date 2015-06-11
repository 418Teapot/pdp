package dk.http418.oconn;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by zeb on 14-05-15.
 */
public class Veggie {

    private String name;
    private int amount;
    private String date;

    private Drawable veggieImg;

    public Drawable getStatusImg() {
        return statusImg;
    }

    public void setStatusImg(Drawable statusImg) {
        this.statusImg = statusImg;
    }

    private Drawable statusImg;

    private boolean isPacked;

    private int collected = 0;

    private boolean hasExtra;
    private int extraAmt;


    public Veggie(String date, String name, int amount) {
        this.date = date;
        this.name = name;
        this.amount = amount;
        this.veggieImg = veggieImg;
        this.isPacked = false;
        this.hasExtra = false;
        this.extraAmt = 0;
    }

    public void setImg(Drawable drawable){
        veggieImg = drawable;
    }

    public String getName() {
        return name;
    }

    public Drawable getImgID() {
        return veggieImg;
    }

    public int getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public boolean isPacked() {
        return isPacked;
    }

    public void setWasPacked(boolean wasPacked) {
        this.isPacked = wasPacked;
    }

    public int getCollected() {
        return collected;
    }

    public void setCollected(int collected) {
        this.collected = collected;
    }

    public boolean hasExtra() {return hasExtra; }
    public void setHasExtra(boolean extra) { hasExtra = extra; }

    public int getExtraAmount(){
        return extraAmt;
    }

    public void setExtraAmt(int eamt){
        extraAmt = eamt;
    }

}
