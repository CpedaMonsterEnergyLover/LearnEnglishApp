package ky.learnenglish.util;

public class Mathf {

    public static int Lerp(float amt, int start, int end){
        if(amt <= 0f) return start;
        else if(amt >= 1f) return end;
        else return Math.round(amt * (end - start));
    }

}
