package app;

import java.util.List;

public class VectorList {
    private List<Integer> columns;
    private List<Integer> syndromeCols;

    public List<Integer> getColumns() {
        return columns;
    }

    public void setColumns(List<Integer> columns) {
        this.columns = columns;
    }

    public List<Integer> getSyndromeCols() {
        return syndromeCols;
    }

    public void setSyndromeCols(List<Integer> syndromeCols) {
        this.syndromeCols = syndromeCols;
    }

    @Override
    public String toString() {
        return "VectorList{" +
                "columns=" + columns +
                ", syndromeCols=" + syndromeCols +
                '}';
    }
}
