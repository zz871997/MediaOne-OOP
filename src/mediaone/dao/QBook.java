package mediaone.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import mediaone.model.Book;
import mediaone.model.Product;

public class QBook extends ConnectManager implements IDataAccess{
	private static List<Product> lsP;
	private static List<Book> lsB;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Book> loadProduct() {
		lsB = new ArrayList<>();
		String query;
		Statement stmt;
		try {
			query = "SELECT * FROM sach";
			stmt = connect.createStatement();
			List<Product> lsP = listProduct();
			ResultSet rs = stmt.executeQuery(query);
			
			String idB;
			while(rs.next()) {
				idB = rs.getString(1);
				for (Product product : lsP) {
					if(product.getIdProduct().equals(idB)) {
						Book book = new Book(idB, product.getNameProduct(), product.getQuantity(), product.getInPrice(),
								product.getOutPrice(), rs.getString(2), rs.getString(3));
						lsB.add(book);
					}
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
		return lsB;
	}

	
	@Override
	public String addProduct(Object product) {
		Book book = (Book) product;
		String query, query1, result = "";
		int row = 0, row1 = 0;
		PreparedStatement pstmt = null, pstmt1 = null;
		query = "INSERT INTO sanpham values(?,?,?,?,?)";
		query1 = "INSERT INTO sach values(?,?,?)";
		try {
			pstmt = connect.prepareStatement(query);
			pstmt.setString(1, book.getIdProduct());
			pstmt.setString(2, book.getNameProduct());
			pstmt.setInt(	3, book.getQuantity());
			pstmt.setDouble(4, book.getOutPrice());
			pstmt.setDouble(5, book.getInPrice());
			
			row = pstmt.executeUpdate();
			if(row != 0) {
				pstmt1 = connect.prepareStatement(query1);
				pstmt1.setString(1, book.getIdProduct());
				pstmt1.setString(2, book.getPublisher());
				pstmt1.setString(3, book.getAuthor());
				
				row1 = pstmt1.executeUpdate();
				if(row1 != 0) {
					result = "Added product successfull!";
				} else {
					delProductID(book.getIdProduct());
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e);
				}
			}
			if(pstmt1 != null) {
				try {
					pstmt1.close();
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e);
				}
			}
		}
		return result;
	}

	@Override
	public String delProductID(String idProduct) {
		String result = "";
		int row = 0;
		Statement stmt = null;
		if(hasProductID(idProduct)) {
			String query = "DELETE FROM sach WHERE MaSach = \'" + idProduct + "\'";
			String query1 = "DELETE FROM sanpham WHERE MaSP = \'" + idProduct + "\'";
			try {
				stmt = connect.createStatement();
				row = stmt.executeUpdate(query);
				if(row != 0) {
					stmt.executeUpdate(query1);
					result = "1 .Product have just been removed out from all of database table";
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e);
			} finally {
				if(stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, e);
					}
				}
			}
		} else {
			result = "0 .Nothing change in data";
		}
		return result;
	}

	@Override
	public boolean hasProductID(String idProduct) {
		Boolean result = false;
	
		List<Book> lsBook = loadProduct();
		for (Book book : lsBook) {
			if(book.getIdProduct().equals(idProduct)) {
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	public List<Product> listProduct() {
		lsP = new ArrayList<>();
		String query = "SELECT * FROM sanpham WHERE MaSP LIKE \'B%\'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				Product p = new Product(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getDouble(4), rs.getDouble(5));
				lsP.add(p);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e);
				}
			}
		}
		return lsP;
	}
}