package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.RegisterDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;

/**
 * Created by Admin on 12/4/2016.
 */
public class AdminRoleAdapter extends RecyclerView.Adapter<AdminRoleAdapter.MyViewHolder> implements Filterable {

    Context context;
    AppGlobals appGlobals;
    private ArrayList<RegisterDetails> changedList;
    private RegisterDetails[] tempList;
    String[] MEMBER_TYPES = null;

    RegisterDetails[] registerDetails = null;
    ArrayAdapter<String> memberTypeAdapter = null;
    final private String TAG = "AdminRoleAdapter";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView memberDetail, memberTypeText;
        MaterialBetterSpinner memberType;

        public MyViewHolder(View view) {
            super(view);
            memberDetail = (TextView) view.findViewById(R.id.memberDetail);
            memberType = (MaterialBetterSpinner) view.findViewById(R.id.memberType);
            memberType.setAdapter(memberTypeAdapter);
            memberTypeText = (TextView) view.findViewById(R.id.memberTypeText);
        }
    }

    public AdminRoleAdapter(RegisterDetails[] regDetail, Context context, ArrayList<RegisterDetails> list) {
        this.context = context;
        this.appGlobals = AppGlobals.getInstance(context);
        this.registerDetails = regDetail;
        this.changedList = list;

        MEMBER_TYPES = context.getResources().getStringArray(R.array.member_list);
        memberTypeAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, MEMBER_TYPES);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_role_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final RegisterDetails regDetail = registerDetails[position];

        holder.memberDetail.setText(regDetail.getName() + "\n" + regDetail.getReg_mob());

        final int pos = Integer.parseInt(regDetail.getUser_type());
        holder.memberType.setText(MEMBER_TYPES[pos]);

        if(regDetail.getReg_mob().equals(appGlobals.sharedPref.getLoginMobile())) {
            holder.memberType.setVisibility(View.GONE);
            holder.memberTypeText.setVisibility(View.VISIBLE);

            holder.memberTypeText.setText(MEMBER_TYPES[pos]);
        } else {
            holder.memberType.setVisibility(View.VISIBLE);
            holder.memberTypeText.setVisibility(View.GONE);
            holder.memberType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    regDetail.setUser_type("" + position);
                    changedList.add(regDetail);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return registerDetails.length;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new
                        FilterResults();

                ArrayList<RegisterDetails> tempResults = new ArrayList<RegisterDetails>();
                if (tempList == null)
                    tempList = registerDetails;
                if (constraint != null) {
                    if (tempList != null && tempList.length > 0) {
                        for (final RegisterDetails friend : tempList) {
                            if (friend.getName().toLowerCase().contains(constraint.toString())) {
                                tempResults.add(friend);
                            }
                        }
                    }

                    final RegisterDetails[] results = new RegisterDetails[tempResults.size()];
                    int i=0;
                    for (final RegisterDetails resClass : tempResults) {
                        results[i] = resClass;
                        ++i;
                    }

                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null) {
//                    friendsList = (ArrayList<RegisterDetails>) results.values;
                    registerDetails = (RegisterDetails[]) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }
}