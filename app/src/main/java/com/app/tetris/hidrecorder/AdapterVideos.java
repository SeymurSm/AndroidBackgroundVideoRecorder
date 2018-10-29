package com.app.tetris.hidrecorder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;



public class AdapterVideos extends SimpleAdapter{
    List<? extends Map<String, ?>> map; // if fails to compile, replace with List<HashMap<String, Object>> map
    String[] from;
    int layout;
    int[] to;
    Context context;
    LayoutInflater mInflater;
    public AdapterVideos(Context context, List<? extends Map<String, ?>> data, // if fails to compile, do the same replacement as above on this line
                                 int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        layout = resource;
        map = data;
        this.from = from;
        this.to = to;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return this.createViewFromResource(position, convertView, parent, layout);
    }

    private View createViewFromResource(int position, View convertView,
                                        ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }

        this.bindView(position, v);

        return v;
    }


    private void bindView(int position, View view) {
        final Map dataSet = map.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = super.getViewBinder();
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
//                        if (data instanceof Integer) {
//                            setViewImage((ImageView) v, (Integer) data);
//                        } else if (data instanceof Bitmap){
//                            setViewImage((ImageView) v, (Bitmap)data);
//                        } else {
//                            setViewImage((ImageView) v, text);
//                        }
                        setViewImage((ImageView) v, (Bitmap)StringToBitMap((String) data));
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }



    private void setViewImage(ImageView v, Bitmap bmp){
        v.setImageBitmap(bmp);
    }


    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


}