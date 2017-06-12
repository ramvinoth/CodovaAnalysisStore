package GUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import AppAnalyzer.Configs;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class TableHelper {
	
	
	
	
	public static void fillTable(String query, TableView<ObservableList<String>> tv){
		
		if(!query.equals("")&&query!=null){
		
		ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
		TableView<ObservableList<String>> tableview =tv;
	

		ResultSet rs;
		
		
			Connection c = null;
			Statement stmt = null;

			try {
				// starten
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:"
						+ Configs.dbName);
				c.setAutoCommit(false);

				stmt = c.createStatement();
				//rs = stmt.executeQuery("SELECT * FROM " + "FOUNDAPPS" + ";");
				rs=stmt.executeQuery(query);

		


		for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
			// We are using non property style for making dynamic table
			final int j = i;
			TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
			col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
				public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
					return new SimpleStringProperty(param.getValue().get(j).toString());
				}
			});

			tableview.getColumns().addAll(col);
			//System.out.println("Column [" + i + "] ");
		}

		/********************************
		 * Data added to ObservableList *
		 ********************************/
		while (rs.next()) {
			// Iterate Row
			ObservableList<String> row = FXCollections.observableArrayList();
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				// Iterate Column
				row.add(rs.getString(i));
			}
			//System.out.println("Row [1] added " + row);
			data.add(row);

		}
	
				
		rs.close();

		// FINALLY ADDED TO TableView
		tableview.setItems(data);
		
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage()+" (wordt opgevangen)");
				//ier miss elert oproepen in main screen...
				GUI.Main.getMainController().ShowAlert("Geen Geldige Query", "Dit is geen geldige query");
			} finally {
				try {
					stmt.close();
					c.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		
		
		
		
	}
		
		
	}
	
	

	
}
