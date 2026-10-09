package lk.ijse.cmjd.movie_booking_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static <T> PageResponse<T> fromList(List<T> allItems, int page, int size) {
        if (allItems == null) {
            allItems = List.of();
        }
        int totalElements = allItems.size();
        int safeSize = size > 0 ? size : 10;
        int safePage = Math.max(page, 0);
        int totalPages = (int) Math.ceil((double) totalElements / safeSize);
        int fromIndex = Math.min(safePage * safeSize, totalElements);
        int toIndex = Math.min(fromIndex + safeSize, totalElements);
        List<T> subList = allItems.subList(fromIndex, toIndex);

        return PageResponse.<T>builder()
                .content(subList)
                .page(safePage)
                .size(safeSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(safePage == 0)
                .last(safePage >= totalPages - 1 || totalPages == 0)
                .build();
    }
}
