package br.eco.wash4me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;
import br.eco.wash4me.entity.Brand;
import br.eco.wash4me.entity.Car;

public class CarActivity extends W4MActivity {
    private List<Brand> brands = new ArrayList<>();
    private AppCompatAutoCompleteTextView txtBrands;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_car);

        setupToolbarClose();

        bindViews();

        setupViews();
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        txtBrands = (AppCompatAutoCompleteTextView) findViewById(R.id.txt_car_brand);
        btnAdd = (Button) findViewById(R.id.btn_save_car);
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        Gson gson = new GsonBuilder().create();
        Brand[] bds = gson.fromJson(loadJSONFromAsset(), Brand[].class);

        brands = Arrays.asList(bds);

        BrandsAdapter brandsAdapter = new BrandsAdapter(context, brands);
        txtBrands.setAdapter(brandsAdapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Car car = new Car();
                car.setBrand("Hyundai");
                car.setModel("Elantra");
                Intent intent = new Intent();
                intent.putExtra("car", new GsonBuilder().create().toJson(car));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public String loadJSONFromAsset() {
        String json = null;

        try {
            InputStream is = getAssets().open("brands.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();

            return null;
        }

        return json;
    }

    class BrandsAdapter extends BaseAdapter implements Filterable {
        List<Brand> mList;
        List<Brand> mSuggestions = new ArrayList<>();
        Context mContext;

        BrandsAdapter(Context ctx, List<Brand> list) {
            mList = list;
            mContext = ctx;
        }

        @Override
        public int getCount() {
            return mSuggestions.size();
        }

        @Override
        public Object getItem(int position) {
            return mSuggestions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mSuggestions.get(position).getId().longValue();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View mView = convertView;
            if (mView == null) {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(android.R.layout.simple_dropdown_item_1line, null);
            }

            TextView suggestion = (TextView) mView.findViewById(android.R.id.text1);
            suggestion.setText(mList.get(position).getName());

            return mView;
        }

        @Override
        public Filter getFilter() {
            return new BrandsFilter();
        }

        private class BrandsFilter extends Filter {

            @Override
            public String convertResultToString(Object resultValue) {
                return ((Brand)resultValue).getName();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                mSuggestions.clear();

                if (mList != null && constraint != null) {
                    //Check for similarities in data from constraint
                    for (Brand value : mList) {
                        if (value.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            mSuggestions.add(value);
                            //Toast.makeText(mContext, value + " contains " + constraint.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(mContext, value + " does not " + constraint.toString(), Toast.LENGTH_LONG).show();

                        }
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mSuggestions;
                    filterResults.count = mSuggestions.size();
                    return filterResults;
                } else {
                    return null;
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
            }
        }
    }
}
