package org.phone_lab.jouler.joulerbase.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.phone_lab.jouler.joulerbase.R;

import java.util.List;

/**
 * Created by xcv58 on 1/21/15.
 */
public class ClientAdapter extends ArrayAdapter<Client> {
    private final Context context;
    private List<Client> list;
    private PackageManager pm;

    public ClientAdapter(Context context, List<Client> clientList) {
        super(context, R.layout.client_item, clientList);
        this.context = context;
        this.list = clientList;
        pm = context.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.client_item, parent, false);

        TextView textView_label = (TextView) rowView.findViewById(R.id.label);
        TextView textView_name = (TextView) rowView.findViewById(R.id.name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.check);

        // set one view
        Client client =  list.get(position);
        textView_name.setText(client.getAppName(pm));
        textView_label.setText(client.getDescription(pm));

        Drawable icon = client.getIcon(pm);
        imageView.setImageDrawable((icon != null) ? icon : context.getResources().getDrawable( R.drawable.ic_launcher ));

//        if (myPackageInfo.inList()) {
//            rowView.setBackgroundColor(0xFFAA66CC);
//            checkBox.setChecked(true);
//        }

        return rowView;
    }
}
