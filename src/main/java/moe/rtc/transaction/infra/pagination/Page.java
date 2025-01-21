package moe.rtc.transaction.infra.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<D> {
    private long currentPage;
    private long pageSize;
    private long totalPage;
    private long totalData;
    private D pagedData;

    public <C> Page (Page<C> copyPage, D data){
        this.currentPage = copyPage.currentPage;
        this.pageSize = copyPage.pageSize;
        this.totalPage = copyPage.totalPage;
        this.totalData = copyPage.totalData;
        this.pagedData = data;
    }
}
