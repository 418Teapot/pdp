package dk.http418.oconn;

import android.graphics.drawable.Drawable;

/**
 * Created by zeb on 14-05-15.
 */
public class Veggie {

    private String name;
    private int amount;
    private String date;

    private Drawable veggieImg;
    private boolean isPacked;

    public Veggie(String date, String name, int amount) {
        this.date = date;
        this.name = name;
        this.amount = amount;
        this.veggieImg = veggieImg;
        this.isPacked = false;
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

}
