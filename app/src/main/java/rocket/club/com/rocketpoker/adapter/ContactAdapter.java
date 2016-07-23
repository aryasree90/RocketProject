package rocket.club.com.rocketpoker.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.ContactClass;

import java.util.ArrayList;

public class ContactAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ContactClass> contactList;
    private LayoutInflater layoutInflater = null;

    public ContactAdapter(Context ctx, ArrayList<ContactClass> list) {
        context = ctx;
        contactList = list;

        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int pos) {
        return contactList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.contact_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.contactName = (TextView) view.findViewById(R.id.contact_name);
            viewHolder.contactNumber = (TextView) view.findViewById(R.id.contact_number);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ContactClass contactClass = contactList.get(pos);

        viewHolder.contactName.setText(contactClass.getContactName());
        viewHolder.contactNumber.setText(contactClass.getPhoneNumber());

        return view;
    }

    class ViewHolder {
        public TextView contactName;
        public TextView contactNumber;
    }
}
