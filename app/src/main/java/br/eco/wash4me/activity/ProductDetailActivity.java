package br.eco.wash4me.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;

public class ProductDetailActivity extends W4MActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
    }
}
