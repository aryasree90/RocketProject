package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.FriendsListClass;
import rocket.club.com.rocketpoker.classes.UserTransaction;
import rocket.club.com.rocketpoker.utils.AppGlobals;

/**
 * Created by Admin on 7/22/2016.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {// implements Filterable {

    Context context;
    AppGlobals appGlobals;
    private UserTransaction[] transList;
    private List<UserTransaction> tempList;
    private View.OnClickListener clickListener = null;
    final private String TAG = "TransactionAdapter";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView transDate, transType, transAmt;

        public MyViewHolder(View view) {
            super(view);
            transDate = (TextView) view.findViewById(R.id.transDate);
            transType = (TextView) view.findViewById(R.id.transType);
            transAmt = (TextView) view.findViewById(R.id.transAmt);
        }
    }

    public TransactionAdapter(UserTransaction[] transList, Context context) {
        this.transList = transList;
        this.context = context;
        this.appGlobals = AppGlobals.getInstance(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_trans_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserTransaction transRow = transList[position];

        long timeStamp = Long.parseLong(transRow.getTimeStamp());

        holder.transDate.setText(AppGlobals.convertDate(timeStamp));
        holder.transType.setText(transRow.getTrans_type());
        holder.transAmt.setText(transRow.getAmount());
    }

    @Override
    public int getItemCount() {
        return transList.length;
    }

    /*@Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<FriendsListClass> results = new ArrayList<FriendsListClass>();
                if (tempList == null)
                    tempList = transList;
                if (constraint != null) {
                    if (tempList != null & tempList.size() > 0) {
                        for (final UserTransaction trans : tempList) {
                            *//*if (friend.getName().toLowerCase().contains(constraint.toString()))
                                results.add(friend);*//*
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null) {
                    transList = (ArrayList<UserTransaction>) results.values;
                    notifyDataSetChanged();
                }

            }
        };
    }*/
}