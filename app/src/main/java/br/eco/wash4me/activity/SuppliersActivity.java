package br.eco.wash4me.activity;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.List;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;
import br.eco.wash4me.entity.Supplier;
import br.eco.wash4me.utils.Callback;

import static br.eco.wash4me.activity.base.W4MApplication.log;
import static br.eco.wash4me.data.DataAccess.getDataAccess;

public class SuppliersActivity extends W4MActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suppliers);

        setupToolbarBack();

        getDataAccess().getSuppliers(context, new Callback<List<Supplier>>() {
            @Override
            public void execute(List<Supplier> suppliers) {
                for(Supplier s : suppliers) {
                    log(s.getName());
                }
            }
        });
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
