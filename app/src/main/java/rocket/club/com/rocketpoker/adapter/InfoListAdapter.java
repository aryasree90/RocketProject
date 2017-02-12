package rocket.club.com.rocketpoker.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocket.club.com.rocketpoker.ConnectionDetector;
import rocket.club.com.rocketpoker.EventDetailActivity;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 10/12/2016.
 */
public class InfoListAdapter extends PagerAdapter {

    Context context;
    final String WHATSAPP = "com.whatsapp", FACEBOOK = "com.viber.voip";
    Dialog dialog = null;

    LayoutInflater mLayoutInflater;
    ArrayList<InfoDetails> infoList = null;
    View.OnClickListener itemClickListener;
    ConnectionDetector connectionDetector = null;
    View.OnClickListener clickListener = null;
    final String VALIDATION_URL = AppGlobals.SERVER_URL + "likeEvent.php";

    AppGlobals appGlobals = null;
    String activityType = null;
    EventDetailActivity eda = null;

    private final String TAG = "InfoListAdapter";

    public InfoListAdapter(Context context, ArrayList<InfoDetails> infoList,
                           View.OnClickListener clickListener, String actType, EventDetailActivity eda) {
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.infoList = infoList;
        connectionDetector = new ConnectionDetector(context);
        appGlobals = AppGlobals.getInstance(context);
        this.clickListener = clickListener;
        this.activityType = actType;
        this.eda = eda;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        int layoutId = R.layout.activity_event;

        if (activityType.equals(AppGlobals.EVENT_INFO)) {
            layoutId = R.layout.activity_event;
        } else if (activityType.equals(AppGlobals.SERVICE_INFO)) {
            layoutId = R.layout.activity_service;
        }

        View itemView = mLayoutInflater.inflate(layoutId, container, false);

        LinearLayout likeShareTool = (LinearLayout) itemView.findViewById(R.id.likeShareTool);
        final ImageButton likeImageBtn = (ImageButton) itemView.findViewById(R.id.likeImage);
        ImageButton shareImageBtn = (ImageButton) itemView.findViewById(R.id.shareImage);
        TextView eventHeader = (TextView) itemView.findViewById(R.id.eventHeaderText);
        TextView eventSubHeader = (TextView) itemView.findViewById(R.id.eventSummaryText);
        final ImageView eventImage = (ImageView) itemView.findViewById(R.id.eventImage);
        TextView imageText = (TextView) itemView.findViewById(R.id.imageText);

        final InfoDetails infoItem = infoList.get(position);

        try {
            String imgPath = AppGlobals.SERVER_URL + infoItem.getInfoImage();
            appGlobals.loadImageFromServer(imgPath, eventImage, imageText, true);
        }catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
        }

        eventHeader.setText(infoItem.getInfoTitle());
        eventSubHeader.setText(infoItem.getInfoSubTitle());

        if (activityType.equals(AppGlobals.EVENT_INFO)) {
            likeShareTool.setVisibility(View.VISIBLE);
            if (appGlobals.sharedPref.getLikeEventList().contains(infoItem.getId())) {
                likeImageBtn.setImageResource(R.mipmap.ic_favorite);
            } else {
                likeImageBtn.setImageResource(R.mipmap.ic_favorite_border);
            }
        } else if(activityType.equals(AppGlobals.SERVICE_INFO)) {
            likeShareTool.setVisibility(View.GONE);
        }

        likeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String likeEventList = appGlobals.sharedPref.getLikeEventList();
                String likeId = infoItem.getId() + ",";
                if (likeEventList.contains(infoItem.getId())) {
                    likeImageBtn.setImageResource(R.mipmap.ic_favorite_border);
                    infoItem.setInfoLikeStatus("false");
                    likeEventList = likeEventList.replace(likeId, "");
                } else {
                    likeImageBtn.setImageResource(R.mipmap.ic_favorite);
                    infoItem.setInfoLikeStatus("true");
                    likeEventList += likeId;
                }
                appGlobals.sharedPref.setLikeEventList(likeEventList);
//                infoAdapter.notifyDataSetChanged();
                Map<String,String> map = new HashMap<String,String>();
                map.put("likeStatus", infoItem.getInfoLikeStatus());
                map.put("likeTimeStamp", infoItem.getInfoTimeStamp());
                map.put("likeId", infoItem.getId());

                serverCall(map);
            }
        });

        shareImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fileName = appGlobals.getRocketsPath(context) + "/tempFile.jpeg";
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(fileName);
                    Bitmap bitmap = ((BitmapDrawable)eventImage.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                shareViaApp(infoItem);

                /*String headerMsg = infoItem.getInfoTitle() + "\n" + infoItem.getInfoSubTitle();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, headerMsg);
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent,
                        context.getResources().getText(R.string.send_to)).
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));*/
            }
        });

        container.addView(itemView);
        return itemView;
    }

    private void shareViaApp(InfoDetails infoItem) {
        if(!connectionDetector.isConnectingToInternet()) {
            appGlobals.toastMsg(context, context.getString(R.string.no_internet), appGlobals.LENGTH_LONG);
            return;
        }

        boolean whatsApp = isAppAvailable(WHATSAPP);
        boolean facebook = isAppAvailable(FACEBOOK);

        if ((whatsApp) && (facebook))
            shownInviteOptionsDialog(0, infoItem);
        else if ((!whatsApp) && (facebook))
            shownInviteOptionsDialog(1, infoItem);
        else if ((whatsApp) && (!facebook))
            shownInviteOptionsDialog(2, infoItem);
        else
            appGlobals.toastMsg(context, context.getString(R.string.app_unavailable), appGlobals.LENGTH_LONG);
    }

    private void shownInviteOptionsDialog(final int appCount, final InfoDetails infoItem) {

        try {
            dialog = new Dialog(eda);
            dialog.setContentView(R.layout.share_via_app_layout);

            dialog.setTitle("Share via");

            GridView gridView = (GridView) dialog.findViewById(R.id.gridView);

            MyAdapter myAdapter = new MyAdapter(context, appCount);
            gridView.setAdapter(myAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapter, View view,
                                        int pos, long id) {

                    switch (pos) {
                        case 0:
                            if (appCount == 1)
                                inviteViaApp(FACEBOOK, infoItem);
                            else
                                inviteViaApp(WHATSAPP, infoItem);
                            break;
                        case 1:
                            inviteViaApp(FACEBOOK, infoItem);
                            break;
                    }
                }
            });

            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
    }


    private void inviteViaApp(String app, InfoDetails infoItem) {

        String fileName = appGlobals.getRocketsPath(context) + "/tempFile.jpeg";
        String picture_text = infoItem.getInfoTitle() + "\n" + infoItem.getInfoSubTitle();
        Uri imageUri = Uri.parse(fileName);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        if(app.equals(WHATSAPP)) {
            //Target whatsapp:
            shareIntent.setPackage("com.whatsapp");
            //Add text and then Image URI
            shareIntent.putExtra(Intent.EXTRA_TEXT, picture_text);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/jpeg");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                context.startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                appGlobals.logClass.setLogMsg(TAG, ex.toString(), LogClass.ERROR_MSG);
            }
        }
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private boolean isAppAvailable(String appName) {
        PackageManager pm = context.getPackageManager();

        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    private void serverCall(final Map<String,String> params) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().contains("success")) {

                        } else {
                            appGlobals.toastMsg(context, context.getString(R.string.pls_try_later), appGlobals.LENGTH_LONG);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    class MyAdapter extends BaseAdapter {
        Context context;
        int appCount;

        int[] shareViaAppImages = {R.drawable.ic_whatsapp, R.drawable.ic_facebook};
        String[] shareViaAppNames = {"WhatsApp", "Facebook"};

        int[] shareViaAppImageswithoutWhatsApp = {R.drawable.ic_facebook};
        String[] shareViaAppNameswithoutWhatsApp = {"Facebook"};

        int[] shareViaAppImageswithoutFb = {R.drawable.ic_whatsapp};
        String[] shareViaAppNameswithoutFb = {"Whatsapp"};

        public MyAdapter(Context c) {
            this.context = c;
        }

        public MyAdapter(Context c, int appCount) {
            this.context = c;
            this.appCount = appCount;
        }

        public int getCount() {
            if (appCount == 0)
                return shareViaAppImages.length;
            else
                return shareViaAppImageswithoutFb.length;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View grid;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                grid = new View(context);
                grid = inflater.inflate(R.layout.share_via_gridview,
                        null);
                TextView textView = (TextView) grid
                        .findViewById(R.id.gridtextview);
                ImageView imageView = (ImageView) grid
                        .findViewById(R.id.gridimageview);

                if (appCount == 0) {
                    textView.setText(shareViaAppNames[position]);
                    imageView.setImageResource(shareViaAppImages[position]);
                } else if (appCount == 1) {
                    textView.setText(shareViaAppNameswithoutWhatsApp[position]);
                    imageView
                            .setImageResource(shareViaAppImageswithoutWhatsApp[position]);
                } else if (appCount == 2) {
                    textView.setText(shareViaAppNameswithoutFb[position]);
                    imageView
                            .setImageResource(shareViaAppImageswithoutFb[position]);
                }
            } else {
                grid = (View) convertView;
            }
            return grid;
        }
    }
}
