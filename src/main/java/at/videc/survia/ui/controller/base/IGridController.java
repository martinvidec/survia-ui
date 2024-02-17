package at.videc.survia.ui.controller.base;

import java.util.List;

public interface IGridController<T> {

    void save(T entityModel);

    void delete(T entityModel);

    int count(Integer page, Integer size);

    List<T> list(Integer page, Integer size, List<String> sort);

}
