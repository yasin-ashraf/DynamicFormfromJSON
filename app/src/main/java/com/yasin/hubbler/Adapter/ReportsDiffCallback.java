package com.yasin.hubbler.Adapter;

import android.support.v7.util.DiffUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yasin.hubbler.Model.Report;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by im_yasinashraf started on 15/11/18.
 * Tried using DiffsCallback for Reports Adapter, not working rn,problem with comparing object?
 */
public class ReportsDiffCallback extends DiffUtil.Callback {

    private List<Report> oldList;
    private List<Report> newList;

    ReportsDiffCallback(List<Report> oldList, List<Report> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return oldList.get(i).getAddedTime().equals(newList.get(i1).getAddedTime());
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        try {
            JSONObject reportObject1 = new JSONObject(oldList.get(i).getReport());
            JSONObject reportObject2 = new JSONObject(oldList.get(i1).getReport());
            JsonParser parser = new JsonParser();
            JsonElement obj1 = parser.parse(reportObject1.toString());
            JsonElement obj2 = parser.parse(reportObject2.toString());
            return Objects.equals(obj1, obj2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
