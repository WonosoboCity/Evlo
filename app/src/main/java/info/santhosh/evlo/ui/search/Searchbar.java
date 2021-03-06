package info.santhosh.evlo.ui.search;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import info.santhosh.evlo.R;
import info.santhosh.evlo.widget.TransformingToolbar;

/**
 * A Toolbar with an EditText used for searching
 */
public class Searchbar extends TransformingToolbar {

    OnTextChangedCallback mOnTextChangedCallback;

    public Searchbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(), R.layout.merge_search, this);
        EditText editText = findViewById(R.id.toolbar_search_edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mOnTextChangedCallback != null) mOnTextChangedCallback.onSearchChange(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void showContent() {
        super.showContent();
        findViewById(R.id.toolbar_search_edittext).requestFocus();
    }

    public void clearText() {
        ((EditText) findViewById(R.id.toolbar_search_edittext)).setText(null);
    }

    public void setTextChangedListener(OnTextChangedCallback OnTextChangedCallback) {
        mOnTextChangedCallback = OnTextChangedCallback;
    }

    interface OnTextChangedCallback {
        void onSearchChange(CharSequence s);
    }

}
