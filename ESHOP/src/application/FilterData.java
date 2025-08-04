package application;

import java.time.LocalDate;

public class FilterData {
    public LocalDate startDate;
    public LocalDate endDate;
    public String userId;
    public String searchText;

    public FilterData(LocalDate start, LocalDate end, String user) {
        this.startDate = start;
        this.endDate = end;
        this.userId = user;
    }
}
