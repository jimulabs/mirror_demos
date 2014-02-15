package com.jimulabs.samples.mirrormail;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matt on 2014-02-13.
 */
public class StaticInboxAdapter extends BaseAdapter {

    private final Context mContext;

    public StaticInboxAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return STATIC_ITEMS.size();
    }

    @Override
    public Object getItem(int position) {
        return STATIC_ITEMS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            v = inflater.inflate(R.layout.mail_list_item, null);

            vh = createViewholder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }

        InboxItem item = (InboxItem) getItem(position);
        vh.avatar.setImageResource(item.avatarId);
        vh.flag.setVisibility(item.flagVisible ? View.VISIBLE : View.GONE);
        vh.time.setText(item.receiveTime);
        vh.bullet.setVisibility(item.bulletVisible ? View.VISIBLE : View.GONE);
        vh.sender.setText(item.sender);
        vh.attachment.setVisibility(item.hasAttachment ? View.VISIBLE : View.GONE);
        vh.subject.setText(item.subject);
        vh.snippet.setText(item.snippet);

        return v;
    }

    private ViewHolder createViewholder(View v) {
        final ViewHolder vh = new ViewHolder();
        vh.avatar = findView(v, R.id.avatar);
        vh.flag = findView(v, R.id.flag);
        vh.time = findView(v, R.id.time);
        vh.bullet = findView(v, R.id.bullet);
        vh.sender = findView(v, R.id.from);
        vh.attachment = findView(v, R.id.attachment);
        vh.subject = findView(v, R.id.subject);
        vh.snippet = findView(v, R.id.snippet);
        vh.tick = findView(v, R.id.tick);


        vh.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                createFlipAnimator(R.animator.card_flip_left_out, R.animator.card_flip_right_in,
                        vh.avatar, vh.tick).start();
            }
        });
        vh.tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                createFlipAnimator(R.animator.card_flip_right_out, R.animator.card_flip_left_in,
                        vh.tick, vh.avatar).start();
            }
        });

        return vh;
    }

    private Animator createFlipAnimator(int animatorOut, int animatorIn, final View outView, final View inView) {
        Context context = outView.getContext();
        final Animator animAvatarOut = AnimatorInflater.loadAnimator(context, animatorOut);
        animAvatarOut.setTarget(outView);
        final Animator animTickIn = AnimatorInflater.loadAnimator(context, animatorIn);
        animTickIn.setTarget(inView);
        animAvatarOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                outView.setVisibility(View.INVISIBLE);
                inView.setVisibility(View.VISIBLE);
                animTickIn.start();
            }
        });
        return animAvatarOut;
    }

    private <T extends View> T findView(View root, int id) {
        return (T) root.findViewById(id);
    }

    private static class ViewHolder {
        CircularImageView avatar;
        View tick;
        ImageView flag;
        TextView time;
        ImageView bullet;
        TextView sender;
        ImageView attachment;
        TextView subject;
        TextView snippet;
    }

    private final static List<InboxItem> STATIC_ITEMS = makeStaticItems();

    private static List<InboxItem> makeStaticItems() {
        List<InboxItem> items = new ArrayList<InboxItem>();
        addItem(items, R.drawable.ava1, false, "17:35", false, "Paul McCartney", false,
                "Have you seen the new version of Mirror?", "I've got to admit it's getting" +
                "better. It's a little better all the time.");
        addItem(items, R.drawable.ava2, false, "13:21", false, "Albert Einstein", false,
                "Relatively speaking", "A person who never made a mistake " +
                "never tried anything new.");
        addItem(items, R.drawable.ava3, false, "yesterday", true, "John F. Kennedy", false,
                "The City Upon a Hill", "Change is the law of life. And those who look only to" +
                "the past or present are certain to miss the future.");
        addItem(items, R.drawable.ava4, true, "yesterday", false, "Elvis Presley", true,
                "Man I love Vegas", "I think I have something tonight that's not quite correct" +
                "for evening wear. Blue suede shoes.");

        return items;
    }

    private static void addItem(List<InboxItem> items, int avatarId, boolean flagVisible,
                                String receiveTime, boolean bulletVisible, String sender,
                                boolean hasAttachment, String subject, String snippet) {
        items.add(new InboxItem(avatarId, flagVisible, receiveTime, bulletVisible, sender,
                hasAttachment, subject, snippet));

    }
}
