package br.eco.wash4me.activity;

import android.os.Bundle;
import android.view.MenuItem;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;

public class ProductsActivity extends W4MActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

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
}
