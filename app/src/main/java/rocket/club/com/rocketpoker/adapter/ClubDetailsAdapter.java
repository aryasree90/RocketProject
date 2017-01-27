package rocket.club.com.rocketpoker.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;

import rocket.club.com.rocketpoker.ClubDetails;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.DetailsListClass;
import rocket.club.com.rocketpoker.classes.RegisterDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;

/**
 * Created by Admin on 12/4/2016.
 */
public class ClubDetailsAdapter extends RecyclerView.Adapter<ClubDetailsAdapter.MyViewHolder> implements Filterable {

    Context context;
    ClubDetails clubDetails = null;
    AppGlobals appGlobals;
    int setId;
    private DetailsListClass[] itemList;
    private DetailsListClass[] tempList;

    final private String TAG = "ClubDetailsAdapter";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView item1, item2, item3;

        public MyViewHolder(View view) {
            super(view);
            item1 = (TextView) view.findViewById(R.id.item1);
            item2 = (TextView) view.findViewById(R.id.item2);
            item3 = (TextView) view.findViewById(R.id.item3);
        }
    }

    public ClubDetailsAdapter(DetailsListClass[] itemList, Context context, ClubDetails details, int setId) {
        this.context = context;
        this.appGlobals = AppGlobals.getInstance(context);
        this.itemList = itemList;
        this.clubDetails = details;
        this.setId = setId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_details_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DetailsListClass detailList = itemList[position];

        holder.item1.setText(detailList.getItem1());
        holder.item2.setText(detailList.getItem2());
        holder.item3.setText(detailList.getItem3());

        holder.item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, detailList.getItem4(), Toast.LENGTH_LONG).show();
                if(clubDetails != null)
                    createMediaDialog(detailList.getItem4(), detailList.getItem5(), detailList.getItem6());
            }
        });
    }

    private void createMediaDialog(String msg1, String msg2, String msg3) {
        Dialog dialog=new Dialog(clubDetails.getActivity());
        dialog.setContentView(R.layout.popup_details);

//        dialog.setTitle(getString(R.string.select_media));

        TextView text1 = (TextView) dialog.findViewById(R.id.text1);
        TextView text2 = (TextView) dialog.findViewById(R.id.text2);
        TextView text3 = (TextView) dialog.findViewById(R.id.text3);

        if(setId == ClubDetails.EXP_ID) {
            text1.setText(context.getString(R.string.description) + " : " + msg1);
            text2.setText(context.getString(R.string.cashier) + " : " + msg2);
        } else {
            text1.setText(context.getString(R.string.paymnt_type) + " : " + msg1);
            text2.setText(context.getString(R.string.month) + " " + AppGlobals.convertDateTime(Long.parseLong(msg2)));
            text3.setText(context.getString(R.string.cashier) + " : " + msg3);
        }

        dialog.show();

        appGlobals.setDialogLayoutParams(dialog, context, false, true);
    }

    @Override
    public int getItemCount() {
        return itemList.length;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new
                        FilterResults();
                ArrayList<DetailsListClass> tempResults = new ArrayList<DetailsListClass>();
                if (tempList == null)
                    tempList = itemList;
                if (constraint != null) {
                    if (tempList != null && tempList.length > 0) {
                        for (final DetailsListClass item : tempList) {

                            String search = constraint.toString().toLowerCase();

                            if(search.equals("na")) {
                                tempResults.add(item);
                            } else {
                                String item1 = item.getItem1().toLowerCase();
                                String item2 = item.getItem2().toLowerCase();
                                String item3 = item.getItem3().toLowerCase();

                                if (item1.contains(search) || item2.contains(search) ||
                                        item3.contains(search)) {
                                    tempResults.add(item);
                                }
                            }
                        }
                    }

                    final DetailsListClass[] results = new DetailsListClass[tempResults.size()];

                    int i=0;
                    for (final DetailsListClass detClass : tempResults) {
                        results[i] = detClass;
                        ++i;
                    }

                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null) {
                    itemList = (DetailsListClass[]) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }
}