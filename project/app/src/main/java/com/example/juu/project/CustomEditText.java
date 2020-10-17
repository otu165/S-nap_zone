package com.example.juu.project;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomEditText extends TextInputLayout {
    private Context context;

    public CustomEditText(Context context) {
        super(context);
        this.context = context;
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        EditText editText = getEditText();
        if(editText != null) {
            editText.setBackground(ContextCompat.getDrawable(this.context, R.drawable.edit_text));
        }
    }

    @Override
    public void setError(@Nullable final CharSequence error) {
        super.setError(error);

        EditText editText = getEditText();
        if(editText != null) {
            editText.setBackground(ContextCompat.getDrawable(this.context, R.drawable.edit_text));
            editText.setText("");
        }
    }
}
