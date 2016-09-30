package br.eco.wash4me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;
import br.eco.wash4me.entity.Product;
import br.eco.wash4me.utils.Callback;

import static br.eco.wash4me.data.DataAccess.getDataAccess;
import static br.eco.wash4me.utils.Constants.TAG_STEP_1_VIEW;
import static br.eco.wash4me.utils.Constants.TAG_STEP_2_VIEW;

public class StepsActivity extends W4MActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RelativeLayout content;
    private Button btnNext;

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
    }

    @Override
    protected void setupViews() {
        setupStep1Views();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStep1Showing()) {
                    setupStep2Views();
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
    }

    private Boolean isStep1Showing() {
        return content.getChildAt(0).getTag().equals(TAG_STEP_1_VIEW);
    }

    class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
        private List<Product> mDataSet;
        private Context mContext;

        public ProductsAdapter(Context context, List<Product> DataSet) {
            mDataSet = DataSet;
            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(View v) {
                super(v);

                mTextView = (TextView) v.findViewById(R.id.tv);
            }
        }

        @Override
        public ProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.product_view, parent, false);
            ViewHolder vh = new ViewHolder(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = recyclerView.indexOfChild(view);

                    ((ProductsAdapter) recyclerView.getAdapter()).getProducts().get(itemPosition);

                    //Intent intent = new Intent(context, OrderDetailActivity.class);
                    //intent.putExtra("id", itemPosition + 1);
                    //startActivity(intent);
                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Product item = mDataSet.get(position);
            holder.mTextView.setText(item.getName());

            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(item.getFeatured());
            //holder.mTextView.getLayoutParams().height = getRandomIntInRange(250,75);
            //holder.mTextView.setBackgroundColor(getRandomHSVColor());
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        public List<Product> getProducts() {
            return mDataSet;
        }
    }
}
