package com.example.quanlylichhoc;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private List<AttendanceModel> list;
    private boolean isTeacher;

    public AttendanceAdapter(List<AttendanceModel> list, boolean isTeacher) {
        this.list = list;
        this.isTeacher = isTeacher;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceModel model = list.get(position);
        if (isTeacher) {
            holder.txtName.setText(model.getStudentName());
            holder.radioGroup.setVisibility(View.VISIBLE);
            holder.txtStatus.setVisibility(View.GONE);
            
            // Đặt đúng trạng thái ban đầu cho radio buttons
            if (model.getStatus().equals("Có mặt")) holder.radioGroup.check(R.id.rbPresent);
            else if (model.getStatus().equals("Vắng")) holder.radioGroup.check(R.id.rbAbsent);
            else if (model.getStatus().equals("Vắng có phép")) holder.radioGroup.check(R.id.rbExcused);

            holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbPresent) model.setStatus("Có mặt");
                else if (checkedId == R.id.rbAbsent) model.setStatus("Vắng");
                else if (checkedId == R.id.rbExcused) model.setStatus("Vắng có phép");
            });
        } else {
            holder.txtName.setText("Ngày: " + model.getDate());
            holder.txtStatus.setText("Trạng thái: " + model.getStatus());
            holder.radioGroup.setVisibility(View.GONE);
            holder.txtStatus.setVisibility(View.VISIBLE);
            
            if (model.getStatus().equals("Có mặt")) holder.txtStatus.setTextColor(Color.parseColor("#43A047"));
            else if (model.getStatus().contains("Vắng")) holder.txtStatus.setTextColor(Color.parseColor("#E53935"));
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtStatus;
        RadioGroup radioGroup;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtStudentName);
            txtStatus = itemView.findViewById(R.id.txtAttendanceStatus);
            radioGroup = itemView.findViewById(R.id.rgStatus);
        }
    }
}
