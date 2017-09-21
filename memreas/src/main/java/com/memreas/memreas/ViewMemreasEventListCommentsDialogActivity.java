package com.memreas.memreas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.memreas.R;
import com.memreas.base.BaseActivity;

public class ViewMemreasEventListCommentsDialogActivity extends BaseActivity {

    public static int requestCode = 1005;
    private Button doneBtn;
    private ListView memreas_event_comment_listview;
    private LinearLayout noCommentsLinearLayout;
    private MemreasEventsCommentsListAdapter memreasEventsCommentsListAdapter;
    private int position = 0;
    private int sub_position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        int contentView = R.layout.memreas_event_list_comments_dialog;
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            contentView = getIntent().getExtras().getInt("contentView");
            position = getIntent().getIntExtra("position", 0);
            sub_position = getIntent().getIntExtra("sub_position", 0);
        }
        setContentView(contentView);

        // Setup Buttons etc...
        doneBtn = (Button) findViewById(R.id.doneBtn);
        memreas_event_comment_listview = (ListView) findViewById(R.id.memreas_event_comment_listview);
        noCommentsLinearLayout = (LinearLayout) findViewById(R.id.memreas_event_comment_ListView_Empty);
        initializeView();

    }

    public void initializeView() {

        doneBtn.setOnClickListener(doneListener);
        memreasEventsCommentsListAdapter = MemreasEventsCommentsListAdapter
                .getInstance(); // comments list should be set by now from calling view...
        memreasEventsCommentsListAdapter
                .setMemreasEventsCommentsListAdapterView(this,
                        R.layout.memreas_event_list_comments_dialog_item);
        if ((memreasEventsCommentsListAdapter != null) && (memreasEventsCommentsListAdapter.getmCommentList() != null) && (memreasEventsCommentsListAdapter.getmCommentList().size() > 0)) {
            memreas_event_comment_listview.setVisibility(View.VISIBLE);
            memreas_event_comment_listview
                    .setAdapter(memreasEventsCommentsListAdapter);
        } else {
            noCommentsLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    OnClickListener doneListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onActivityResult(
                    ViewMemreasEventListCommentsDialogActivity.requestCode,
                    RESULT_OK, null);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
