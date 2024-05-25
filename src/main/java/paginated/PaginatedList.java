package paginated;

import java.util.Collection;
import java.util.List;

public class PaginatedList<T> {
    private static int totalPageNumber;

    private static <T> int getTotalPages(Collection<T> items, int pageSize) {
        return (int) Math.ceil((double) items.size() / pageSize);
    }

    private static void checkPageNumber(int page, int pageSize, int totalPageNumber) {
        if (page < 1 || pageSize < 1)
            throw new IllegalArgumentException("Invalid page number");
        if (totalPageNumber == 0)
            throw new IllegalArgumentException("No items found");
        if (page > totalPageNumber)
            throw new IllegalArgumentException("Invalid page number");
    }

    public static <T> List<T> getPageItems(Collection<T> items, int page, int pageSize) {
        totalPageNumber = getTotalPages(items, pageSize);
        checkPageNumber(page, pageSize, totalPageNumber);
        return items.stream()
                .skip((page - 1) * pageSize)
                .limit(pageSize).toList();
    }
}