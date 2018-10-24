package com.app.tetris.hidrecorder;

/**
 * Created by seymur on 13.05.2016.
 */
import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new LoremViewsFactory(this.getApplicationContext(),
                intent));
    }
}