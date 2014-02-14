package com.jimulabs.samples.mirrormail;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by matt on 2014-02-13.
 */
public class ReadMailFragment extends Fragment {
    public static final String ARG_ITEM = "inbox_item";
    private InboxItem mItem;

    public ReadMailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_read, container, false);

        if (getArguments() != null && getArguments().containsKey(ARG_ITEM)) {
            mItem = getArguments().getParcelable(ARG_ITEM);
        }

        Log.i("MailListFragment", "mItem null? " + (mItem == null));

        if (mItem != null) {
            fillView(rootView, mItem);
        }

        return rootView;
    }

    private void fillView(View v, InboxItem item) {
        Log.i("MailListFragment", "filling views from item");
        ((TextView) v.findViewById(R.id.subject)).setText(item.subject);
//        setText(v, R.id.subject, item.subject);
        log("Subject: " + item.subject);
        setText(v, R.id.timestamp, item.receiveTime);
        log("Time: " + item.receiveTime);
        setText(v, R.id.sender_name, item.sender);
        log("Sender: " + item.sender);
        v.findViewById(R.id.attachments_container)
                .setVisibility(item.hasAttachment ? View.VISIBLE : View.GONE);
        setText(v, R.id.email_text, item.snippet);
    }

    private void log(String s) {
        Log.i("MailListFragment", s);
    }

    private void setText(View root, int textId, String content) {
        TextView tv = (TextView) root.findViewById(textId);
        tv.setText(content);
    }
}
