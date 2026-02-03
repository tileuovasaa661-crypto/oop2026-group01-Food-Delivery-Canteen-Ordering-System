package repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface Repository<T, ID> {
    ID create(Connection conn, T entity) throws SQLException;
    Optional<T> findById(Connection conn, ID id) throws SQLException;
}
