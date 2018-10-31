package com.app.tetris.hidrecorder;

/**
 * Created by SeymurElk on 10/27/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class ListAdapterHomeMain extends ArrayAdapter<VideoFile> {
    ICallback ic;
    private final Context context;
    private final ArrayList<VideoFile> itemsArrayList;

    public ListAdapterHomeMain(Context context, ArrayList<VideoFile> itemsArrayList,ICallback callback) {

        super(context, R.layout.list_back, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
        this.ic = callback;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_back, parent, false);

        TextView videoDate = (TextView) rowView.findViewById(R.id.tvDate);
        ImageView videoImage = (ImageView) rowView.findViewById(R.id.ivVideoImage);
        ImageButton playButton = (ImageButton) rowView.findViewById(R.id.btnPlay);
        ImageButton shareButton = (ImageButton) rowView.findViewById(R.id.btnShare);
        ImageButton deleteButton = (ImageButton) rowView.findViewById(R.id.btnDelete);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ic.onPlayEvent(position);
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ic.onShareEvent(position);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ic.onDeleteEvent(position);
            }
        });

        // 4. Set the text for textView
        Typeface fontuc = Typeface.createFromAsset(context.getAssets(), "fonts/uicksandregular.otf");
        videoDate.setTypeface(fontuc);
        videoDate.setText(itemsArrayList.get(position).getFileDate());
        videoImage.setImageBitmap(getRoundedCornerBitmap(itemsArrayList.get(position).getFileImage(),8));
        //Glide.with(context).load(itemsArrayList.get(position).get_event_image_url()).into(eventImage);

        return rowView;
    }


    public void refreshEvents(ArrayList<VideoFile> events) {
        this.itemsArrayList.clear();
        this.itemsArrayList.addAll(events);
        notifyDataSetChanged();
    }

    public interface ICallback {

        void onPlayEvent(int position);
        void onShareEvent(int position);
        void onDeleteEvent(int position);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


}
