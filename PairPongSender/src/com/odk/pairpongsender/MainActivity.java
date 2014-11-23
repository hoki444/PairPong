package com.odk.pairpongsender;

import android.app.Activity;
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
        findViewById(R.id.sendint).setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        // bind QPair Service
    	if(v==findViewById(R.id.sample_start_activity)){
    		sfunction.setpackage("com.odk.pairpong");
    		sfunction.startreceiver("PairPongBoardActivity");
    	}
    	else
    		sfunction.sendint(Integer.valueOf(((TextView)findViewById(R.id.sample_int_extra)).getText().toString()));
    }

}
