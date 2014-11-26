package com.odk.pairpongsender;

import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.odk.pairpongsender.game.MainGame;
import com.odk.pairpongsender.game.ReceiverFunction;
import com.odk.pairpongsender.game.SenderFunction;

public class ControllerActivity extends AndroidApplication {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		SenderFunction sfunction = new QPairSenderFunction(this);
		ReceiverFunction rfunction = new QPairReceiverFunction();

        sfunction.setpackage("com.odk.pairpong");
        initialize(new MainGame(sfunction, rfunction));
    }
}
