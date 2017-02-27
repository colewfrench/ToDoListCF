package com.example.colefrench.todolist;

import android.content.Context;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Cole French on 2/10/2017.
 */

public abstract class ToDoView extends LinearLayout {

    private static final int ENTRY_TOP_MARGIN = 10; // 10 px

    private static final int CHECK_BOX_LEFT_MARGIN = 15; // 15 px

    protected int ENTRY_TEXT_COLOR;

    private Context context;

    // instance variables for the views of a ToDoEntry
    private CheckBox entryCheckBox;
    private TextView entryTextView;

    public ToDoView(Context context, String initEntryText, CompoundButton.OnCheckedChangeListener l)
    {
        super(context);

        ENTRY_TEXT_COLOR = R.color.EntryTextColorLight;

        this.context = context;

        initThisEntryLayout();

        entryCheckBox = initEntryCheckBox(l);
        entryTextView = initEntryTextView(initEntryText);

        this.addView(entryCheckBox);
        this.addView(entryTextView);
    }

    private void initThisEntryLayout()
    {
        int entryLayoutWidth = (int)getResources().getDimension(R.dimen.HTC_One_m8_entry_width); // 1080px
        int entryLayoutHeight = (int)getResources().getDimension(R.dimen.HTC_One_m8_entry_height); // 96px

        LinearLayout.LayoutParams thisEntryLayoutParams = new LinearLayout.LayoutParams(entryLayoutWidth, entryLayoutHeight);
        thisEntryLayoutParams.setMargins(0, ENTRY_TOP_MARGIN, 0, 0);
        this.setLayoutParams(thisEntryLayoutParams);
        this.setGravity(Gravity.CENTER_VERTICAL);
        this.setOrientation(HORIZONTAL);

        this.setBackgroundResource(R.drawable.entry_border);
    }

    private CheckBox initEntryCheckBox(CompoundButton.OnCheckedChangeListener listener)
    {
        CheckBox newCheckBox = new CheckBox(context);
        newCheckBox.setText("");
        newCheckBox.setChecked(false);
        LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        checkBoxParams.setMargins(CHECK_BOX_LEFT_MARGIN, 0, 0, 0);
        newCheckBox.setLayoutParams(checkBoxParams);
        newCheckBox.setOnCheckedChangeListener(listener);

        return newCheckBox;
    }

    // TODO either get rid of the default setting or add set default functionality in app
    private TextView initEntryTextView()
    {
        TextView newTextView = setupTextView();

        newTextView.setText(R.string.default_entry_text);
        return newTextView;
    }

    private TextView initEntryTextView(String text)
    {
        TextView newTextView = setupTextView();

        newTextView.setText(text);
        return newTextView;
    }

    private TextView setupTextView()
    {
        TextView newTextView = new TextView(this.context);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        newTextView.setLayoutParams(textViewParams);
        newTextView.setTextSize(getResources().getDimension(R.dimen.text_view_text_size));
        newTextView.setTextColor(ENTRY_TEXT_COLOR);
        newTextView.setGravity(Gravity.CENTER_VERTICAL);

        return newTextView;
    }

    public void setEntryText(String text)
    {
        entryTextView.setText(text);
    }

    public String getEntryText()
    {
        return (String)entryTextView.getText();
    }

    public void setChecked(boolean checked)
    {
        entryCheckBox.setChecked(checked);
    }
}
