package com.yasin.hubbler.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yasin.hubbler.Activity.ViewReportActivity;
import com.yasin.hubbler.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by im_yasinashraf started on 16/11/18.
 */
public class ViewReportFragment extends Fragment {

    private LinearLayout containerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_report, container, false);
        initViews(view);
        String report = ((ViewReportActivity)Objects.requireNonNull(getActivity())).getReport();
        ArrayList<String> fields = Objects.requireNonNull(getArguments()).getStringArrayList("fields");
        createViews(report,Objects.requireNonNull(fields));
        return view;
    }

    private void initViews(View view){
        containerView = view.findViewById(R.id.container);
    }

    private void createViews(String report, ArrayList<String> fields) {
        try {
            JSONObject reportObject = new JSONObject(report);
            for (int i = 0; i < fields.size(); i++) {
                containerView.addView(createFields(fields.get(i),reportObject.getString(fields.get(i))));
                containerView.addView(createDividerView());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private RelativeLayout createFields(String fieldName, String field) {
        RelativeLayout relativeLayout = new RelativeLayout(getActivity());
        relativeLayout.setLayoutParams(getGeneralLayoutParams());
        TextView[] textView = new TextView[2];
        textView[0] = new TextView(getActivity()); //first TextView for Field Name
        textView[0].setId(R.id.textViewId);
        textView[0].setTextSize(16);
        textView[0].setTextAppearance(getActivity(),R.style.regularStyle);
        textView[0].setText(fieldName);
        relativeLayout.addView(textView[0],getTextViewLayoutParams());
        textView[1] = new TextView(getActivity()); //second TextView for Field
        textView[1].setTextSize(18);
        textView[1].setTextAppearance(getActivity(),R.style.boldStyle);
        //Remove braces and unwanted characters from string.
        String finalText = field
                .replace("{","")
                .replace("}","")
                .replace("\"","")
                .replace(",",", ");
        textView[1].setText(finalText);

        RelativeLayout.LayoutParams layoutParams = getGeneralLayoutParams();
        layoutParams.addRule(RelativeLayout.RIGHT_OF, textView[0].getId()); //align to the right of first textView
        relativeLayout.addView(textView[1],layoutParams);
        return relativeLayout;
    }

    private View createDividerView(){
        View view = new View(getActivity());
        view.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.hint));
        view.setLayoutParams(getViewLayoutParams());
        return view;
    }

    private RelativeLayout.LayoutParams getGeneralLayoutParams() {
        RelativeLayout.LayoutParams generalLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        generalLayoutParams.setMargins(75, 25, 50, 25);
        return generalLayoutParams;
    }

    private RelativeLayout.LayoutParams getTextViewLayoutParams() {
        RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {// add extra margin for devices below Marshmallow
            textViewLayoutParams.setMargins(75, 25, 50, 25);
        }
        textViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        return textViewLayoutParams;
    }

    private LinearLayout.LayoutParams getViewLayoutParams() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
    }

}
