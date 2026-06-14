package com.example.quanlylichhoc.storage;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.example.quanlylichhoc.models.News;

public class NewsViewModel extends ViewModel {
    private final SavedStateHandle state;

    public NewsViewModel(SavedStateHandle savedStateHandle) {
        this.state = savedStateHandle;
    }

    public void setDraftTitle(String title) { state.set("draft_title", title); }
    public String getDraftTitle() { return state.get("draft_title"); }

    public void setDraftContent(String content) { state.set("draft_content", content); }
    public String getDraftContent() { return state.get("draft_content"); }
    
    public void setDialogShowing(boolean isShowing) { state.set("is_dialog_showing", isShowing); }
    public Boolean isDialogShowing() { return state.get("is_dialog_showing"); }

    public void setEditingNews(News news) { state.set("editing_news", news); }
    public News getEditingNews() { return state.get("editing_news"); }
}
