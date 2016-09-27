package br.eco.wash4me.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;
import br.eco.wash4me.data.DataAccess;
import br.eco.wash4me.entity.Order;
import br.eco.wash4me.utils.Callback;

import static br.eco.wash4me.data.DataAccess.getDataAccess;

public class MyOrdersActivity extends W4MActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private FloatingActionButton btnNewOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_orders);

        bindViews();

        setupViews();

        setupToolbarMenu();

        setupNavigationDrawer();
    }

    @Override
    protected void bindViews() {
        recyclerView = (RecyclerView) findViewById(R.id.my_orders_list);
        btnNewOrder = (FloatingActionButton) findViewById(R.id.fab_new_order);
    }

    @Override
    protected void setupViews() {
        RecyclerView.LayoutManager recyclerLayoutManager = new LinearLayoutManager(MyOrdersActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        DataAccess dataAccess = getDataAccess();
        dataAccess.getOrders(context, new Callback<List<Order>>() {
            @Override
            public void execute(List<Order> orders) {
                recyclerAdapter = new OrdersAdapter(MyOrdersActivity.this, orders);
                recyclerView.setAdapter(recyclerAdapter);
            }
        });

        btnNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyOrdersActivity.this, OrderDetailActivity.class));
            }
        });
    }

    class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
        private List<Order> mDataSet;
        private Context mContext;

        public OrdersAdapter(Context context, List<Order> DataSet) {
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
        public OrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.order_view, parent, false);
            ViewHolder vh = new ViewHolder(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = recyclerView.indexOfChild(view);

                    ((OrdersAdapter) recyclerView.getAdapter()).getOrders().get(itemPosition);

                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("id", itemPosition + 1);
                    startActivity(intent);
                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Order item = mDataSet.get(position);
            holder.mTextView.setText(item.toString());
            //holder.mTextView.getLayoutParams().height = getRandomIntInRange(250,75);
            //holder.mTextView.setBackgroundColor(getRandomHSVColor());
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        public List<Order> getOrders() {
            return mDataSet;
        }
    }
}