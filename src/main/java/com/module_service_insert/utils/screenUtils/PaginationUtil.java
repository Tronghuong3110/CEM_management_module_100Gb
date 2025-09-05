package com.module_service_insert.utils.screenUtils;

import javafx.collections.ObservableList;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

/**
 * @author Trọng Hướng
 */
public class PaginationUtil<T> {
    private final Integer ITEM_PER_PAGE = 5;
    private final double HEADER_HEIGHT = 30.0;
    private final double MAX_TABLE_HEIGHT = 400.0;

    public Pagination createPagination(TableView<T> tableView, ObservableList<T> pagedData, ObservableList<T> currentData) {
        Pagination pagination = new Pagination();
        updatePagination(pagination, pagedData, currentData, tableView);
        pagination.getStylesheets().add(
                PaginationUtil.class.getResource("/com/module_service_insert/css/pagination.css").toExternalForm()
        );
        return pagination;
    }

    public void updatePagination(Pagination pagination, ObservableList<T> pagedData,
                                 ObservableList<T> currentData, TableView<T> tableView) {
        int pageCount = (int) Math.ceil((double) currentData.size() / ITEM_PER_PAGE);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setCurrentPageIndex(0);

        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * ITEM_PER_PAGE;
            int toIndex = Math.min(fromIndex + ITEM_PER_PAGE, currentData.size());

            if (fromIndex <= toIndex) {
                pagedData.setAll(currentData.subList(fromIndex, toIndex));
            } else {
                pagedData.clear();
            }

            updateTableHeight(tableView);
            return new VBox(tableView);
        });
    }

    private void updateTableHeight(TableView<T> table) {
        table.setMaxHeight(MAX_TABLE_HEIGHT);
        table.setMinHeight(HEADER_HEIGHT);
    }
}

