package org.phone_lab.jouler.joulerbase.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.phone_lab.jouler.joulerbase.R;

import java.util.List;

/**
 * Created by xcv58 on 1/21/15.
 */
public class ClientAdapter extends ArrayAdapter<Client> {
    private final Context context;
    private List<Client> list;

    public ClientAdapter(Context context, List<Client> clientList) {
        super(context, R.layout.client_item, clientList);
        this.context = context;
        this.list = clientList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.client_item, parent, false);

        TextView textView_label = (TextView) rowView.findViewById(R.id.label);
        TextView textView_name = (TextView) rowView.findViewById(R.id.name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
        RadioButton radioButton = (RadioButton) rowView.findViewById(R.id.check);

        // set one view
        Client client =  list.get(position);
        textView_name.setText(client.getAppName());
        textView_label.setText(client.getDescription());
        if (client.isSelected()) {
            textView_label.setVisibility(View.VISIBLE);
        }
        if (client.isChoosed()) {
            radioButton.setChecked(true);
        }
        radioButton.setOnClickListener(client.clientClickListener);

        Drawable icon = client.getIcon();
        imageView.setImageDrawable((icon != null) ? icon : context.getResources().getDrawable( R.drawable.ic_launcher ));

        return rowView;
    }
}
