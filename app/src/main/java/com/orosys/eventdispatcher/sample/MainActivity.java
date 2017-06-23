package com.orosys.eventdispatcher.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.oro.scheduler.AbstractEventDispatcherService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initScheduler();

        findViewById(R.id.click_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ClickEvent().trigger(MainActivity.this);
            }
        });
    }

    private void initScheduler() {
        AbstractEventDispatcherService.addEvent(this, ClickEvent.class);
        AbstractEventDispatcherService.addEvent(this, LoopEvent.class);
        AbstractEventDispatcherService.addAction(this, EventActor.class);

        AbstractEventDispatcherService.startEvent(this);
    }
}
