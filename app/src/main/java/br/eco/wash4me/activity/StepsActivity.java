package br.eco.wash4me.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;
import br.eco.wash4me.entity.Car;
import br.eco.wash4me.entity.OrderRequest;
import br.eco.wash4me.entity.Product;
import br.eco.wash4me.utils.Callback;

import static br.eco.wash4me.data.DataAccess.getDataAccess;
import static br.eco.wash4me.utils.Constants.TAG_STEP_1_VIEW;
import static br.eco.wash4me.utils.Constants.TAG_STEP_2_VIEW;
import static br.eco.wash4me.utils.Constants.TAG_STEP_3_VIEW;

public class StepsActivity extends W4MActivity {
    public static final int REQUEST_CODE_CAR = 0;
    private TextView title;
    private RecyclerView gridProducts;
    private RecyclerView.Adapter productsAdapter;
    private RecyclerView gridMonth;
    private RelativeLayout content;
    private Button btnNext;
    private View progress;
    private OrderRequest request;

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
            setupToolbarClose();
        }

        showTotal();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isStep2Showing()) {
            setupStep1Views();

            return true;
        } else if (isStep3Showing()) {
            setupStep2Views();

            return true;
        } else {
            if (getW4MApplication().isLogged(context)) {
                return super.onOptionsItemSelected(item);
            } else {
                finish();

                startActivity(new Intent(StepsActivity.this, LoginActivity.class));

                return true;
            }
        }
    }

    @Override
    protected void bindViews() {
        content = (RelativeLayout) findViewById(R.id.content);
        btnNext = (Button) findViewById(R.id.btn_next);
        progress = findViewById(R.id.progress_circle);
        title = (TextView) findViewById(R.id.txt_title_toolbar_steps);

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
                    if (request.calculatePrice() > 0.0) {
                        setupStep3Views();
                    } else {
                        Snackbar.make(findViewById(R.id.drawer_layout), "Escolha ao menos um serviço.", Snackbar.LENGTH_LONG)
                                .setAction("ENTENDI", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                })
                                .show();
                    }
                } else {
                    if (request.getDate().after(new GregorianCalendar(new Locale("pt", "BR")))) {
                        startActivity(new Intent(StepsActivity.this, OrderDetailActivity.class));
                    } else {
                        Snackbar.make(findViewById(R.id.drawer_layout), "Escolha uma data posterior a hoje.", Snackbar.LENGTH_LONG)
                                .setAction("ENTENDI", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                })
                                .show();
                    }
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CAR) {
            if (resultCode == RESULT_OK) {
                String carJSON = data.getStringExtra("car");

                Gson json = new GsonBuilder().create();
                Car car = json.fromJson(carJSON, Car.class);

                getW4MApplication().getOrderRequest().setCar(car);

                checkCarViews();
            }
        }
    }

    private void setupStep1Views() {
        progress.setVisibility(View.VISIBLE);

        LayoutInflater inflater = getLayoutInflater();

        content.removeAllViews();

        RelativeLayout step1 = (RelativeLayout) inflater.inflate(R.layout.step1, content, false);
        step1.setTag(TAG_STEP_1_VIEW);
        content.addView(step1);

        btnNext.setText("DEFINIR LOCAL");

        findViewById(R.id.btn_add_car).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(context, CarActivity.class), REQUEST_CODE_CAR);
            }
        });

        findViewById(R.id.txt_more_info).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    ((ScrollView) findViewById(R.id.scroll_main)).fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });

        checkTitleStep1();

        progress.setVisibility(View.GONE);

        if (getW4MApplication().isLogged(context)) {
            setupToolbarMenu();
        } else {
            setupToolbarClose();
        }

        title.setText("Escolha o Local");

        checkCarViews();
    }

    private void checkCarViews() {
        View carContainer = findViewById(R.id.car_container);
        View carEmptyContainer = findViewById(R.id.car_empty_container);

        carContainer.setVisibility(View.GONE);
        carEmptyContainer.setVisibility(View.VISIBLE);

        if (getW4MApplication().isLogged(context) &&
                !getW4MApplication().getLoggedUser(context).getMyCars().isEmpty()) {
            carContainer.setVisibility(View.VISIBLE);
            carEmptyContainer.setVisibility(View.GONE);

            Car first = getW4MApplication().getLoggedUser(context).getMyCars().get(0);

            getW4MApplication().getOrderRequest().setCar(first);
        }

        Car car = getW4MApplication().getOrderRequest().getCar();
        if (car == null) {
            hideBtnNext();
        } else {
            String carTitle = car.getBrand() + " " + car.getModel();
            ((TextView) findViewById(R.id.txt_car_main)).setText(carTitle);

            showBtnNext();
        }
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
        RecyclerView.LayoutManager recyclerLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
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

        title.setText("Escolha o Serviço");

        if (request.calculatePrice() > 0.0) {
            showBtnNext();
        } else {
            hideBtnNext();
        }
    }

    private void toggleTitleStep2(Boolean check) {
        toggleTitleStep3(false);

        if (check) {
            getSupportActionBar().setTitle("Escolha os Serviços");

            ((TextView) findViewById(R.id.title_step_2)).setTextColor(
                    ContextCompat.getColor(context, R.color.w4mTextColorInverse));
            findViewById(R.id.title_step_2).setBackgroundColor(
                    ContextCompat.getColor(context, R.color.w4mPrimary));
            ((ImageView) findViewById(R.id.arrow_2)).setColorFilter(
                    ContextCompat.getColor(context, R.color.w4mPrimary));
        } else {
            ((TextView) findViewById(R.id.title_step_2)).setTextColor(
                    ContextCompat.getColor(context, R.color.w4mTextColorInactive));
            findViewById(R.id.title_step_2).setBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.transparent));
            ((ImageView) findViewById(R.id.arrow_2)).setColorFilter(
                    ContextCompat.getColor(context, android.R.color.white));
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

        findViewById(R.id.nav_next_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GregorianCalendar date = getW4MApplication().getOrderRequest().getDate();
                Integer newMonth = date.get(Calendar.MONTH) + 1;
                date.set(Calendar.MONTH, newMonth);
                ((TextView) findViewById(R.id.month_title)).setText(date.getDisplayName(Calendar.MONTH,
                        Calendar.LONG, new Locale("pt", "BR")));

                setupGridMonth();
            }
        });

        findViewById(R.id.nav_previous_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GregorianCalendar date = getW4MApplication().getOrderRequest().getDate();
                Integer newMonth = date.get(Calendar.MONTH) - 1;
                date.set(Calendar.MONTH, newMonth);
                ((TextView) findViewById(R.id.month_title)).setText(date.getDisplayName(Calendar.MONTH,
                        Calendar.LONG, new Locale("pt", "BR")));

                setupGridMonth();
            }
        });

        btnNext.setText("AGENDAR DATA");

        progress.setVisibility(View.GONE);

        setupToolbarBack();

        title.setText("Escolha a Data");

        final GridLayout hours = (GridLayout) findViewById(R.id.grid_hours);

        for (int i = 0; i < hours.getChildCount(); i++) {
            hours.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < hours.getChildCount(); i++) {
                        ((RelativeLayout) hours.getChildAt(i)).getChildAt(1).setVisibility(View.GONE);
                    }

                    RelativeLayout hour = (RelativeLayout) view;
                    hour.getChildAt(1).setVisibility(View.VISIBLE);

                    String choosedHour = ((TextView) hour.getChildAt(0)).getText().toString();

                    ((TextView) findViewById(R.id.hour_title)).setText(choosedHour);
                }
            });
        }

        hours.getChildAt(0).callOnClick();
    }

    private void setupGridMonth() {
        List<GregorianCalendar> days = new ArrayList<>();

        GregorianCalendar date = getW4MApplication().getOrderRequest().getDate();

        Integer currentMonth = new GregorianCalendar(new Locale("pt", "BR")).get(Calendar.MONTH);
        Integer choosedMonth = date.get(Calendar.MONTH);
        Integer startDay = date.getActualMinimum(Calendar.DAY_OF_MONTH);

        if (choosedMonth.equals(currentMonth)) {
            startDay = new GregorianCalendar(new Locale("pt", "BR")).get(Calendar.DAY_OF_MONTH);
        }

        for (int i = startDay + 1; i <= (date.getActualMaximum(Calendar.DAY_OF_MONTH) + 1); i++) {
            GregorianCalendar day = new GregorianCalendar(new Locale("pt", "BR"));

            day.set(Calendar.MONTH, choosedMonth);
            day.set(Calendar.DAY_OF_MONTH, (i - 1));
            days.add(day);
        }

        MonthAdapter monthAdapter = new MonthAdapter(context, days);

        gridMonth = (RecyclerView) findViewById(R.id.month_days);
        RecyclerView.LayoutManager recyclerLayoutManager = new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.HORIZONTAL);
        gridMonth.setLayoutManager(recyclerLayoutManager);
        gridMonth.setAdapter(monthAdapter);

        gridMonth.scrollToPosition(0);

        monthObservers.clear();
    }

    private void toggleTitleStep3(Boolean check) {
        if (check) {
            ((TextView) findViewById(R.id.title_step_3)).setTextColor(
                    ContextCompat.getColor(context, R.color.w4mTextColorInverse));
            findViewById(R.id.title_step_3).setBackgroundColor(
                    ContextCompat.getColor(context, R.color.w4mPrimary));
        } else {
            ((TextView) findViewById(R.id.title_step_3)).setTextColor(
                    ContextCompat.getColor(context, R.color.w4mTextColorInactive));
            findViewById(R.id.title_step_3).setBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.transparent));
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

    private void showBtnNext() {
        btnNext.setVisibility(View.VISIBLE);
    }

    private void hideBtnNext() {
        btnNext.setVisibility(View.GONE);
    }

    static List<Observable> monthObservers = new ArrayList<>();

    interface Observable {
        void execute();
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

                    if (request.calculatePrice() > 0.0) {
                        showBtnNext();
                    } else {
                        hideBtnNext();
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

            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
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

        class ViewHolder extends RecyclerView.ViewHolder implements Observable {
            TextView title;
            TextView day;
            View checkedIcon;
            GregorianCalendar date;

            ViewHolder(View v) {
                super(v);

                title = (TextView) v.findViewById(R.id.day_title);
                day = (TextView) v.findViewById(R.id.day_number);
                checkedIcon = v.findViewById(R.id.checked_icon);
            }

            @Override
            public void execute() {
                checkedIcon.setVisibility(View.GONE);
            }
        }

        @Override
        public MonthAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.month_day_view, parent, false);
            final ViewHolder vh = new ViewHolder(v);
            monthObservers.add(vh);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = gridMonth.indexOfChild(view);

                    GregorianCalendar day = vh.date;

                    for (Observable o : monthObservers) {
                        o.execute();
                    }

                    GregorianCalendar date = getW4MApplication().getOrderRequest().getDate();
                    date.set(Calendar.DAY_OF_MONTH, day.get(Calendar.DAY_OF_MONTH));

                    view.findViewById(R.id.checked_icon).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.when_day)).setText(
                            Integer.valueOf(day.get(Calendar.DAY_OF_MONTH)).toString());
                    ((TextView) findViewById(R.id.week_day)).setText(date.getDisplayName(
                            Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR")));

                    Log.i("w4m.app", "[MonthAdapter.onCreateViewHolder] Setting pos " + itemPosition + ", day " +
                            day.get(Calendar.DAY_OF_MONTH) + " - " + date.getDisplayName(Calendar.DAY_OF_WEEK,
                            Calendar.LONG, new Locale("pt", "BR")).toUpperCase());
                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            GregorianCalendar item = getDays().get(position);

            holder.date = item;

            String day = Integer.valueOf(item.get(Calendar.DAY_OF_MONTH)).toString();
            String title = item.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
                    new Locale("pt", "BR"));

            holder.day.setText(day);
            holder.title.setText(title.split("-")[0]);

            if (position == 0) {
                holder.checkedIcon.setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.when_day)).setText(day);
                ((TextView) findViewById(R.id.week_day)).setText(title);
            } else {
                holder.checkedIcon.setVisibility(View.GONE);
            }

            Log.i("w4m.app", "[MonthAdapter.onBindViewHolder] Creating pos " + position + ", day " +
                    day + " - " + title.toUpperCase());
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mDataSet.get(position).get(Calendar.DAY_OF_MONTH);
        }

        List<GregorianCalendar> getDays() {
            return mDataSet;
        }
    }
}
