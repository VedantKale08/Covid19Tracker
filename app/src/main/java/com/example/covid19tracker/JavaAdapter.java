package com.example.covid19tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class JavaAdapter extends RecyclerView.Adapter<JavaAdapter.ViewHolder> {

    List<Model> records;
    public JavaAdapter (List<Model>records){
        this.records=records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.testing,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String statename = records.get(position).getState();
        String covidcases = records.get(position).getCases();
        String covidrecovered = records.get(position).getRecovered();
        String coviddeaths = records.get(position).getDeaths();

        holder.setData(statename,covidcases,covidrecovered,coviddeaths);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView t1,t2,t3,t4;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(R.id.textview1);
            t2 = itemView.findViewById(R.id.textview3);
            t3 = itemView.findViewById(R.id.textview5);
            t4 = itemView.findViewById(R.id.textview7);

        }

        public void setData(String statename, String covidcases, String covidrecovered, String coviddeaths) {
            t1.setText(statename);
            t2.setText(covidcases);
            t3.setText(covidrecovered);
            t4.setText(coviddeaths);
        }
    }
}
