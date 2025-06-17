package com.CodeLab.DB_Service.responseDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class PaginatedResponse<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalItems;

//    public PaginatedResponse(List<T> content, int currentPage, int totalPages, long totalItems) {
//        this.content = content;
//        this.currentPage = currentPage;
//        this.totalPages = totalPages;
//        this.totalItems = totalItems;
//    }

    // Getters & Setters
}
