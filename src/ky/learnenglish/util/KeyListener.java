package ky.learnenglish.util;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import ky.learnenglish.forms.MainForm;

public class KeyListener implements NativeKeyListener {

    public void nativeKeyPressed(NativeKeyEvent e) {
        if(MainForm.Instance == null) return;
        if(e.getKeyCode() == NativeKeyEvent.VC_SPACE){
            MainForm.Instance.SetPause(!MainForm.paused);
        } else if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            MainForm.Instance.Close();
        }
    }

}