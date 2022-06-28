package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;

public class ArtsmiaDAO {

	public void listObjects(HashMap<Integer,ArtObject> idMap) {
		
		String sql = "SELECT * from objects";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(!idMap.containsKey(res.getInt("object_id"))) {

					ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
					idMap.put(artObj.getId(), artObj);
				}
			}
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			
		}
	}

	public List<Adiacenza> getAdiacenze(HashMap<Integer,ArtObject> idMap) {
		
		String sql = "SELECT e1.object_id AS e1, e2.object_id AS e2, COUNT (*) AS peso "
				+ "FROM exhibition_objects e1, exhibition_objects e2 "
				+ "WHERE e1.exhibition_id = e2.exhibition_id "
				+ "AND e1.object_id > e2.object_id " //con il maggiore escludo i duplicati!
				+ "GROUP BY e1.object_id, e2.object_id";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			List<Adiacenza> result = new ArrayList<Adiacenza>();
			
			while(res.next()) { //se c'è lo prendo, serve if e non while
				result.add(new Adiacenza(idMap.get(res.getInt("e1")), 
						idMap.get(res.getInt("e2")), res.getInt("peso")));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
