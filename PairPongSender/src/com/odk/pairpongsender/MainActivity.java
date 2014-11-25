package com.odk.pairpongsender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {
	SenderFunction sfunction= new SenderFunction(this);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        findViewById(R.id.sample_start_activity).setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        // bind QPair Service
    	sfunction.setpackage("com.odk.pairpong");
    	sfunction.startreceiver("PairPongBoardActivity");
    	Intent intent = new Intent(this, ControllerActivity.class);
    	
    	startActivity(intent);
    }

}
