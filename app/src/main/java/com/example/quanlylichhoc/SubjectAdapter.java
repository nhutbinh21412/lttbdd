package com.example.quanlylichhoc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    private List<Subject> subjectList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Subject subject);
    }

    public SubjectAdapter(List<Subject> subjectList, OnItemClickListener listener) {
        this.subjectList = subjectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    //tao khung cho mon hoc
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.txtName.setText(subject.getName());
        holder.txtClassCode.setText(subject.getClassCode() + " - " + subject.getId());
        holder.txtLessonTime.setText("Tiết: " + subject.getLesson() + " | Giờ: " + subject.getTime());
        holder.txtRoom.setText("Phòng: " + subject.getRoom());
        holder.txtTeacher.setText("GV: " + subject.getTeacher());
        holder.txtDay.setText(subject.getDayOfWeek());
        
        // Đặt màu cho vạch bên trái
        holder.viewColorTag.setBackgroundColor(subject.getColor());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(subject));
    }

    @Override
    public int getItemCount() { return subjectList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtClassCode, txtLessonTime, txtRoom, txtTeacher, txtDay;
        View viewColorTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtSubjectName);
            txtClassCode = itemView.findViewById(R.id.txtClassCode);
            txtLessonTime = itemView.findViewById(R.id.txtLessonTime);
            txtRoom = itemView.findViewById(R.id.txtRoom);
            txtTeacher = itemView.findViewById(R.id.txtTeacher);
            txtDay = itemView.findViewById(R.id.txtDay);
            viewColorTag = itemView.findViewById(R.id.viewColorTag);
        }
    }
}