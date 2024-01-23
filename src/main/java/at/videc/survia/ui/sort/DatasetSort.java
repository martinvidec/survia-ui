package at.videc.survia.ui.sort;

public class DatasetSort {

    private final String propertyName;
    private final boolean descending;

    public DatasetSort(String propertyName, boolean descending) {
        this.propertyName = propertyName;
        this.descending = descending;
    }
}
