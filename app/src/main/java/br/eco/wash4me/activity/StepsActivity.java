package br.eco.wash4me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;

public class StepsActivity extends W4MActivity {
    private RelativeLayout content;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        bindViews();

        setupViews();

        setupToolbarBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void bindViews() {
        content = (RelativeLayout) findViewById(R.id.content);
        btnNext = (Button) findViewById(R.id.btn_next);
    }

    @Override
    protected void setupViews() {
        final LayoutInflater inflater = getLayoutInflater();
        final RelativeLayout step1 = (RelativeLayout) inflater.inflate(R.layout.step1, content, false);
        content.addView(step1);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(content.getChildAt(0).equals(step1)) {
                    content.removeAllViews();

                    RelativeLayout step2 = (RelativeLayout) inflater.inflate(R.layout.step2, content, false);
                    content.addView(step2);
                } else {
                    StepsActivity.this.finish();

                    startActivity(new Intent(StepsActivity.this, OrderDetailActivity.class));
                }
            }
        });
    }
}
