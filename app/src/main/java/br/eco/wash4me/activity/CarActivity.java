package br.eco.wash4me.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;
import br.eco.wash4me.entity.Brand;
import br.eco.wash4me.entity.Car;
import br.eco.wash4me.entity.Model;
import br.eco.wash4me.utils.Callback;

import static br.eco.wash4me.data.DataAccess.getDataAccess;

public class CarActivity extends W4MActivity {
    private List<Brand> brands = new ArrayList<>();
    private AppCompatAutoCompleteTextView txtBrands;
    private AppCompatAutoCompleteTextView txtModels;
    private EditText txtPlateChar;
    private EditText txtPlateNumber;
    private AppCompatSpinner txtColor;
    private Button btnAdd;
    private ImageView brandImage;
    private CardView small;
    private CardView medium;
    private CardView large;

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
        txtModels = (AppCompatAutoCompleteTextView) findViewById(R.id.txt_car_model);
        btnAdd = (Button) findViewById(R.id.btn_save_car);
        brandImage = (ImageView) findViewById(R.id.brand_image);
        txtPlateChar = (EditText) findViewById(R.id.txt_plate_char);
        txtPlateNumber = (EditText) findViewById(R.id.txt_plate_number);
        txtColor = (AppCompatSpinner) findViewById(R.id.txt_color);
        small = (CardView) findViewById(R.id.size_small);
        medium = (CardView) findViewById(R.id.size_medium);
        large = (CardView) findViewById(R.id.size_large);
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        Gson gson = new GsonBuilder().create();
        Brand[] bds = gson.fromJson(loadJSONFromAsset(), Brand[].class);

        brands = Arrays.asList(bds);

        BrandsAdapter brandsAdapter = new BrandsAdapter(context, brands);
        txtBrands.setAdapter(brandsAdapter);
        txtBrands.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Brand brand = (Brand) adapterView.getAdapter().getItem(i);

                AssetManager mg = getResources().getAssets();
                InputStream input;

                try {
                    input = mg.open("brands/" + brand.getName().toLowerCase() + ".jpg");
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(input);

                    Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
                    brandImage.setImageBitmap(bmp);
                } catch (IOException ex) {
                    brandImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.example_brand));
                }

                getDataAccess().getCarModels(context, brand.getName(), new Callback<List<Model>>() {
                    @Override
                    public void execute(List<Model> models) {
                        ModelAdapter modelsAdapter = new ModelAdapter(context, models);
                        txtModels.setAdapter(modelsAdapter);
                        txtModels.setText("");
                        txtModels.requestFocus();
                    }
                });
            }
        });

        txtModels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard(txtModels.getWindowToken());
            }
        });

        txtPlateChar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() > 2) {
                    txtPlateNumber.requestFocus();
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Car car = new Car();
                car.setBrand(txtBrands.getText().toString());
                car.setModel(txtModels.getText().toString());
                car.setPlate(txtPlateChar.getText().toString() + "-" +
                        txtPlateNumber.getText().toString());
                car.setColor(txtColor.getSelectedItem().toString());

                if(findViewById(R.id.selected_size_small).getVisibility() == View.VISIBLE) {
                    car.setSize("S");
                } else if(findViewById(R.id.selected_size_medium).getVisibility() == View.VISIBLE) {
                    car.setSize("M");
                } else if(findViewById(R.id.selected_size_large).getVisibility() == View.VISIBLE) {
                    car.setSize("L");
                }

                if(car.getBrand().isEmpty() || car.getModel().isEmpty() ||
                        txtPlateChar.getText().toString().isEmpty() || txtPlateNumber.getText().toString().isEmpty()) {
                    Snackbar.make((View) btnAdd.getParent(), "Preencha todos os campos corretamente.",
                            Snackbar.LENGTH_LONG).setAction("ENTENDI", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) { }
                    }).show();
                } else {
                    getW4MApplication().addCar(context, car);

                    Intent intent = new Intent();
                    intent.putExtra("car", new GsonBuilder().create().toJson(car));
                    setResult(RESULT_OK, intent);

                    finish();
                }
            }
        });

        txtColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView spinnerText = (TextView) view;
                if(spinnerText == null) return;
                spinnerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                spinnerText.setTextColor(ContextCompat.getColor(context, R.color.w4mDarkAlpha));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.selected_size_small).setVisibility(View.VISIBLE);
                findViewById(R.id.selected_size_medium).setVisibility(View.GONE);
                findViewById(R.id.selected_size_large).setVisibility(View.GONE);
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.selected_size_small).setVisibility(View.GONE);
                findViewById(R.id.selected_size_medium).setVisibility(View.VISIBLE);
                findViewById(R.id.selected_size_large).setVisibility(View.GONE);
            }
        });

        large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.selected_size_small).setVisibility(View.GONE);
                findViewById(R.id.selected_size_medium).setVisibility(View.GONE);
                findViewById(R.id.selected_size_large).setVisibility(View.VISIBLE);
            }
        });
    }

    public String loadJSONFromAsset() {
        String json = null;

        try {
            InputStream is = getAssets().open("brands/brands.json");
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
                mView.setPadding(20, 20, 20, 20);
            }

            TextView suggestion = (TextView) mView.findViewById(android.R.id.text1);
            suggestion.setText(mSuggestions.get(position).getName());

            return mView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                public String convertResultToString(Object resultValue) {
                    return ((Brand)resultValue).getName();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    mSuggestions.clear();

                    if (mList != null && constraint != null) {
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
                    notifyDataSetChanged();
                }
            };
        }
    }

    class ModelAdapter extends BaseAdapter implements Filterable {
        List<Model> mList;
        List<Model> mSuggestions = new ArrayList<>();
        Context mContext;

        ModelAdapter(Context ctx, List<Model> list) {
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
            return mSuggestions.get(position).getN().hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View mView = convertView;
            if (mView == null) {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(android.R.layout.simple_dropdown_item_1line, null);
                mView.setPadding(20, 20, 20, 20);
            }

            TextView suggestion = (TextView) mView.findViewById(android.R.id.text1);
            suggestion.setText(mSuggestions.get(position).getN());

            return mView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                public String convertResultToString(Object resultValue) {
                    return ((Model)resultValue).getN();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    mSuggestions.clear();

                    if (mList != null && constraint != null) {
                        for (Model value : mList) {
                            if (value.getN().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
                    notifyDataSetChanged();
                }
            };
        }
    }
}
