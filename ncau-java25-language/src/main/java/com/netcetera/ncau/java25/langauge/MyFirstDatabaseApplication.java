

// module import declarations
import module java.base;
import module java.sql;

// compact source file, no class

// instance main method
void main() throws IOException, SQLException {
  List<Table> tables = new ArrayList<>();
  
  try (var connection = DriverManager.getConnection("jdbc:h2:mem:");
       var preparedStatement = connection.prepareStatement("""
                                                           SELECT table_name, table_type
                                                             FROM information_schema.tables
                                                            ORDER BY table_name
                                                           """);
       var resultSet = preparedStatement.executeQuery()) {

    while (resultSet.next()) {
      // multi source, in a different .java file
      var table = Table.fromResultSet(resultSet);
      tables.add(table);
    }

  }

  // new IO class
  IO.println(tables);
}

