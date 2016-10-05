package br.eco.wash4me.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;
import br.eco.wash4me.entity.OrderRequest;
import br.eco.wash4me.entity.Product;
import br.eco.wash4me.utils.Callback;

import static br.eco.wash4me.data.DataAccess.getDataAccess;
import static br.eco.wash4me.utils.Constants.TAG_STEP_1_VIEW;
import static br.eco.wash4me.utils.Constants.TAG_STEP_2_VIEW;
import static br.eco.wash4me.utils.Constants.TAG_STEP_3_VIEW;

public class StepsActivity extends W4MActivity {
    private RecyclerView gridProducts;
    private RecyclerView.Adapter productsAdapter;
    private RecyclerView gridMonth;
    private MonthAdapter monthAdapter;
    private RelativeLayout content;
    private Button btnNext;
    private View progress;
    private OrderRequest request;
    private ImageButton btnNextMonth;
    private ImageButton btnPreviousMonth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        bindViews();

        setupViews();

        if (getW4MApplication().isLogged(context)) {
            setupToolbarMenu();

            setupNavigationDrawer();
        } else {
            setupToolbarBack();
        }

        showTotal();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getW4MApplication().isLogged(context)) {
            if (isStep2Showing()) {
                setupStep1Views();

                return true;
            } else if (isStep3Showing()) {
                setupStep2Views();

                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        } else {
            int id = item.getItemId();

            switch (id) {
                case android.R.id.home:
                    finish();

                    startActivity(new Intent(StepsActivity.this, LoginActivity.class));

                    return true;
            }

            return false;
        }
    }

    @Override
    protected void bindViews() {
        content = (RelativeLayout) findViewById(R.id.content);
        btnNext = (Button) findViewById(R.id.btn_next);
        progress = findViewById(R.id.progress_circle);

        request = getW4MApplication().getOrderRequest();
    }

    @Override
    protected void setupViews() {
        setupStep1Views();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStep1Showing()) {
                    setupStep2Views();
                } else if (isStep2Showing()) {
                    setupStep3Views();
                } else {
                    startActivity(new Intent(StepsActivity.this, OrderDetailActivity.class));
                }
            }
        });
    }

    private void setupStep1Views() {
        progress.setVisibility(View.VISIBLE);

        LayoutInflater inflater = getLayoutInflater();

        content.removeAllViews();

        RelativeLayout step1 = (RelativeLayout) inflater.inflate(R.layout.step1, content, false);
        step1.setTag(TAG_STEP_1_VIEW);
        content.addView(step1);

        checkTitleStep1();

        btnNext.setText("DEFINIR LOCAL");

        setupToolbarMenu();

        progress.setVisibility(View.GONE);
    }

    private void checkTitleStep1() {
        toggleTitleStep2(false);
        toggleTitleStep3(false);
    }

    private void setupStep2Views() {
        progress.setVisibility(View.VISIBLE);

        LayoutInflater inflater = getLayoutInflater();

        content.removeAllViews();

        RelativeLayout step2 = (RelativeLayout) inflater.inflate(R.layout.step2, content, false);
        step2.setTag(TAG_STEP_2_VIEW);
        content.addView(step2);

        toggleTitleStep2(true);

        gridProducts = (RecyclerView) content.findViewById(R.id.products_list);
        RecyclerView.LayoutManager recyclerLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridProducts.setLayoutManager(recyclerLayoutManager);

        getDataAccess().getProducts(context, new Callback<List<Product>>() {
            @Override
            public void execute(List<Product> products) {
                progress.setVisibility(View.GONE);

                productsAdapter = new ProductsAdapter(context, products);
                gridProducts.setAdapter(productsAdapter);
            }
        });

        btnNext.setText("PEDIR SERVIÇOS");

        setupToolbarBack();
    }

    private void toggleTitleStep2(Boolean check) {
        toggleTitleStep3(false);

        if (check) {
            ((TextView) findViewById(R.id.title_step_2)).setTextColor(ContextCompat.getColor(context, R.color.w4mTextColorInverse));
            findViewById(R.id.title_step_2).setBackgroundColor(ContextCompat.getColor(context, R.color.w4mPrimary));
            ((ImageView) findViewById(R.id.arrow_2)).setColorFilter(ContextCompat.getColor(context, R.color.w4mPrimary));
        } else {
            ((TextView) findViewById(R.id.title_step_2)).setTextColor(ContextCompat.getColor(context, R.color.w4mTextColorInactive));
            findViewById(R.id.title_step_2).setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            ((ImageView) findViewById(R.id.arrow_2)).setColorFilter(ContextCompat.getColor(context, android.R.color.white));
        }
    }

    private void setupStep3Views() {
        progress.setVisibility(View.VISIBLE);

        LayoutInflater inflater = getLayoutInflater();

        content.removeAllViews();

        RelativeLayout step3 = (RelativeLayout) inflater.inflate(R.layout.step3, content, false);
        step3.setTag(TAG_STEP_3_VIEW);
        content.addView(step3);

        toggleTitleStep3(true);

        setupGridMonth();

        btnNextMonth = (ImageButton) findViewById(R.id.nav_next_month);
        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GregorianCalendar date = getW4MApplication().getOrderRequest().getDate();
                Integer newMonth = date.get(Calendar.MONTH) + 1;
                date.set(Calendar.MONTH, newMonth);
                ((TextView) findViewById(R.id.month_title)).setText(date.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("pt", "BR")));

                setupGridMonth();
            }
        });

        btnPreviousMonth = (ImageButton) findViewById(R.id.nav_previous_month);
        btnPreviousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GregorianCalendar date = getW4MApplication().getOrderRequest().getDate();
                Integer newMonth = date.get(Calendar.MONTH) - 1;
                date.set(Calendar.MONTH, newMonth);
                ((TextView) findViewById(R.id.month_title)).setText(date.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("pt", "BR")));

                setupGridMonth();
            }
        });

        btnNext.setText("AGENDAR DATA");

        progress.setVisibility(View.GONE);
    }

    private void setupGridMonth() {
        List<GregorianCalendar> days = new ArrayList<>();

        GregorianCalendar date = getW4MApplication().getOrderRequest().getDate();

        Integer currentMonth = new GregorianCalendar(new Locale("pt", "BR")).get(Calendar.MONTH);
        Integer choosedMonth = date.get(Calendar.MONTH);
        Integer startDay = date.getActualMinimum(Calendar.DAY_OF_MONTH);

        if(choosedMonth.equals(currentMonth)) {
            startDay = new GregorianCalendar(new Locale("pt", "BR")).get(Calendar.DAY_OF_MONTH);
        }

        for (int i = startDay; i <= date.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            GregorianCalendar day = new GregorianCalendar(new Locale("pt", "BR"));

            day.set(Calendar.MONTH, currentMonth);
            day.set(Calendar.DAY_OF_MONTH, i);
            days.add(day);
        }

        if (monthAdapter == null) {
            monthAdapter = new MonthAdapter(context, days);

            gridMonth = (RecyclerView) findViewById(R.id.month_days);
            RecyclerView.LayoutManager recyclerLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
            gridMonth.setLayoutManager(recyclerLayoutManager);
            gridMonth.setAdapter(monthAdapter);
        } else {
            monthAdapter.setDays(days);

            for (int itemPos = 0; itemPos < gridMonth.getChildCount(); itemPos++) {
                View checkedDayView = gridMonth.getChildAt(itemPos);
                checkedDayView.findViewById(R.id.checked_icon).setVisibility(View.GONE);
            }
        }
    }

    private void toggleTitleStep3(Boolean check) {
        if (check) {
            ((TextView) findViewById(R.id.title_step_3)).setTextColor(ContextCompat.getColor(context, R.color.w4mTextColorInverse));
            findViewById(R.id.title_step_3).setBackgroundColor(ContextCompat.getColor(context, R.color.w4mPrimary));
        } else {
            ((TextView) findViewById(R.id.title_step_3)).setTextColor(ContextCompat.getColor(context, R.color.w4mTextColorInactive));
            findViewById(R.id.title_step_3).setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }
    }

    private Boolean isStep1Showing() {
        return content.getChildAt(0).getTag().equals(TAG_STEP_1_VIEW);
    }

    private Boolean isStep2Showing() {
        return content.getChildAt(0).getTag().equals(TAG_STEP_2_VIEW);
    }

    private Boolean isStep3Showing() {
        return content.getChildAt(0).getTag().equals(TAG_STEP_3_VIEW);
    }

    private void showTotal() {
        Double total = request.calculatePrice();

        if (total > 0.0) {
            ((TextView) findViewById(R.id.total_price)).setText(String.format("R$%d", total.intValue()));
        } else {
            ((TextView) findViewById(R.id.total_price)).setText("R$0");
        }
    }

    class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
        private List<Product> mDataSet;
        private Context mContext;
        private StorageReference storageReference;

        ProductsAdapter(Context context, List<Product> DataSet) {
            mDataSet = DataSet;
            mContext = context;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView productTitle;
            TextView productDescription;
            TextView productPrice;
            ImageView productPicture;
            ImageView downloadIcon;
            View checkedIcon;

            ViewHolder(View v) {
                super(v);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                storageReference = storage.getReferenceFromUrl("gs://wash4me-caafc.appspot.com/products");

                productTitle = (TextView) v.findViewById(R.id.product_title);
                productDescription = (TextView) v.findViewById(R.id.product_description);
                productPrice = (TextView) v.findViewById(R.id.product_price);
                productPicture = (ImageView) v.findViewById(R.id.product_picture);
                downloadIcon = (ImageView) v.findViewById(R.id.download_icon);
                checkedIcon = v.findViewById(R.id.checked_icon);
            }
        }

        @Override
        public ProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.product_view, parent, false);
            final ViewHolder vh = new ViewHolder(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = gridProducts.indexOfChild(view);

                    Product product = ((ProductsAdapter) gridProducts.getAdapter()).getProducts().get(itemPosition);

                    Boolean check = !isThisProductSelected(product);

                    if (check) {
                        request.getProducts().add(product);

                        view.findViewById(R.id.checked_icon).setVisibility(View.VISIBLE);
                    } else {
                        request.getProducts().remove(product);

                        view.findViewById(R.id.checked_icon).setVisibility(View.GONE);
                    }

                    showTotal();
                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Product item = getProducts().get(position);
            holder.productTitle.setText(item.getName());
            holder.productDescription.setText(item.getDescription());
            holder.productPrice.setText(String.format("R$%d", item.getPrice().intValue()));

            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(item.getFeatured());

            StorageReference pictureReference = storageReference.child(item.getId() + ".jpg");

            final long ONE_MEGABYTE = 1024 * 1024;
            pictureReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    holder.productPicture.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    holder.downloadIcon.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            if (isThisProductSelected(item)) {
                holder.checkedIcon.setVisibility(View.VISIBLE);
            } else {
                holder.checkedIcon.setVisibility(View.GONE);
            }

            showTotal();
        }

        private Boolean isThisProductSelected(Product product) {
            return request.getProducts().contains(product);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        List<Product> getProducts() {
            return mDataSet;
        }
    }

    class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {
        private List<GregorianCalendar> mDataSet;
        private Context mContext;

        MonthAdapter(Context context, List<GregorianCalendar> DataSet) {
            mDataSet = DataSet;
            mContext = context;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView day;

            ViewHolder(View v) {
                super(v);

                title = (TextView) v.findViewById(R.id.day_title);
                day = (TextView) v.findViewById(R.id.day_number);
            }
        }

        @Override
        public MonthAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.month_day_view, parent, false);
            final ViewHolder vh = new ViewHolder(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = gridMonth.indexOfChild(view);

                    GregorianCalendar day = ((MonthAdapter) gridMonth.getAdapter()).getDays().get(itemPosition);

                    ViewGroup parent = (ViewGroup) view.getParent();
                    for (int itemPos = 0; itemPos < parent.getChildCount(); itemPos++) {
                        View checkedDayView = parent.getChildAt(itemPos);
                        checkedDayView.findViewById(R.id.checked_icon).setVisibility(View.GONE);
                    }

                    GregorianCalendar date = getW4MApplication().getOrderRequest().getDate();
                    date.set(Calendar.DAY_OF_MONTH, day.get(Calendar.DAY_OF_MONTH));

                    view.findViewById(R.id.checked_icon).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.when_day)).setText(Integer.valueOf(day.get(Calendar.DAY_OF_MONTH)).toString());
                    ((TextView) findViewById(R.id.week_day)).setText(date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR")));
                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            GregorianCalendar item = getDays().get(position);

            holder.day.setText(Integer.valueOf(item.get(Calendar.DAY_OF_MONTH)).toString());
            holder.title.setText(item.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR")).split("-")[0]);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        List<GregorianCalendar> getDays() {
            return mDataSet;
        }

        void setDays(List<GregorianCalendar> DataSet) {
            this.mDataSet = DataSet;

            notifyDataSetChanged();
        }
    }
}
