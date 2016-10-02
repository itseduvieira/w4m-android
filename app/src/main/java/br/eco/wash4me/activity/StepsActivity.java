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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

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
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RelativeLayout content;
    private Button btnNext;
    private View progress;
    private OrderRequest request;
    private TextView txtTitleStep1;
    private TextView txtTitleStep2;
    private TextView txtTitleStep3;
    private GridView gridMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        bindViews();

        setupViews();

        if(getW4MApplication().isLogged(context)) {
            setupToolbarMenu();

            setupNavigationDrawer();
        } else {
            setupToolbarBack();
        }

        showTotal();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(getW4MApplication().isLogged(context)) {
            return super.onOptionsItemSelected(item);
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
        txtTitleStep1 = (TextView) findViewById(R.id.title_step_1);
        txtTitleStep2 = (TextView) findViewById(R.id.title_step_2);
        txtTitleStep3 = (TextView) findViewById(R.id.title_step_3);
        request = getW4MApplication().getOrderRequest();
        gridMonth = (GridView) findViewById(R.id.month_days);
    }

    @Override
    protected void setupViews() {
        setupStep1Views();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStep1Showing()) {
                    setupStep2Views();
                } else if(isStep2Showing()) {
                    setupStep3Views();
                } else {
                    StepsActivity.this.finish();

                    startActivity(new Intent(StepsActivity.this, OrderDetailActivity.class));
                }
            }
        });
    }

    private void setupStep1Views() {
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout step1 = (RelativeLayout) inflater.inflate(R.layout.step1, content, false);
        step1.setTag(TAG_STEP_1_VIEW);
        content.addView(step1);

        recyclerView = (RecyclerView) content.findViewById(R.id.products_list);
        RecyclerView.LayoutManager recyclerLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        getDataAccess().getProducts(context, new Callback<List<Product>>() {
            @Override
            public void execute(List<Product> products) {
                progress.setVisibility(View.GONE);

                recyclerAdapter = new ProductsAdapter(context, products);
                recyclerView.setAdapter(recyclerAdapter);
            }
        });
    }

    private void setupStep2Views() {
        LayoutInflater inflater = getLayoutInflater();

        content.removeAllViews();

        RelativeLayout step2 = (RelativeLayout) inflater.inflate(R.layout.step2, content, false);
        step2.setTag(TAG_STEP_2_VIEW);
        content.addView(step2);

        findViewById(R.id.selector_step_2).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_step_2)).setTextColor(ContextCompat.getColor(context, R.color.w4mPrimary));

        for(int i = 1; i < 20; i++) {

        }

        btnNext.setText("AGENDAR SERVIÇO");
    }

    private void setupStep3Views() {
        LayoutInflater inflater = getLayoutInflater();

        content.removeAllViews();

        RelativeLayout step3 = (RelativeLayout) inflater.inflate(R.layout.step3, content, false);
        step3.setTag(TAG_STEP_3_VIEW);
        content.addView(step3);

        findViewById(R.id.selector_step_3).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_step_3)).setTextColor(ContextCompat.getColor(context, R.color.w4mPrimary));

        btnNext.setText("DEFINIR LOCAL");
    }

    private Boolean isStep1Showing() {
        return content.getChildAt(0).getTag().equals(TAG_STEP_1_VIEW);
    }

    private Boolean isStep2Showing() {
        return content.getChildAt(0).getTag().equals(TAG_STEP_2_VIEW);
    }

    private void showTotal() {
        Double total = request.calculatePrice();

        if(total > 0.0) {
            txtTitleStep1.setText(String.format("TOTAL R$%d", total.intValue()));
        } else {
            txtTitleStep1.setText("SERVIÇOS");
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
            View checkedIcon;
            ImageView downloadIcon;

            ViewHolder(View v) {
                super(v);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                storageReference = storage.getReferenceFromUrl("gs://wash4me-caafc.appspot.com/products");

                productTitle = (TextView) v.findViewById(R.id.product_title);
                productDescription = (TextView) v.findViewById(R.id.product_description);
                productPrice = (TextView) v.findViewById(R.id.product_price);
                productPicture = (ImageView) v.findViewById(R.id.product_picture);
                checkedIcon = v.findViewById(R.id.checked_icon);
                downloadIcon = (ImageView) v.findViewById(R.id.download_icon);
            }
        }

        @Override
        public ProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.product_view, parent, false);
            final ViewHolder vh = new ViewHolder(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = recyclerView.indexOfChild(view);

                    Product product = ((ProductsAdapter) recyclerView.getAdapter()).getProducts().get(itemPosition);

                    Boolean check = !isThisProductSelected(product);

                    if(check) {
                        request.getProducts().add(product);
                    } else {
                        request.getProducts().remove(product);
                    }

                    toggleProductSelection(check, vh, product);

                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Product item = mDataSet.get(position);
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

            toggleProductSelection(isThisProductSelected(item), holder, item);
        }

        private void toggleProductSelection(Boolean select, ProductsAdapter.ViewHolder vh, Product product) {
            if(select) {
                vh.checkedIcon.setVisibility(View.VISIBLE);
            } else {
                vh.checkedIcon.setVisibility(View.GONE);
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
}
