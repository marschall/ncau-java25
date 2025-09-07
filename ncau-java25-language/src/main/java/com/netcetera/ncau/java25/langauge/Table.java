import module java.sql;

record Table(String name, String type) {

  static Table fromResultSet(ResultSet resultSet) throws SQLException {
    String name = resultSet.getString("table_name");
    String type = resultSet.getString("table_type");
    return new Table(name, type);
  }

}
