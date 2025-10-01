package app.daos;

import java.util.List;

public interface IDAO<T, I> {


    T create(T t);

    T getById(I id);

    T update(I i, T t);

    boolean delete(I id);

    List<T> getAll();

}
